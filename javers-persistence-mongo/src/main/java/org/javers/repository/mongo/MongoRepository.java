package org.javers.repository.mongo;

import com.mongodb.*;
import com.mongodb.util.JSON;
import org.bson.BSONObject;
import org.javers.common.collections.Optional;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.clazz.Entity;
import org.javers.core.metamodel.clazz.ManagedClass;
import org.javers.core.metamodel.clazz.ValueObject;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.mongo.model.MongoHeadId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.javers.common.validation.Validate.conditionFulfilled;

/**
 * @author pawel szymczyk
 */
public class MongoRepository implements JaversRepository {

    private static final int DESC = -1;
    private static final int ASC = 1;
    public static final String SNAPSHOTS = "jv_snapshots";
    public static final String COMMIT_ID = "commitMetadata.id";
    public static final String GLOBAL_ID_KEY = "globalId_key";
    public static final String GLOBAL_ID_ENTITY = "globalId.entity";
    public static final String GLOBAL_ID_OWNER_ID_ENTITY = "globalId.ownerId.entity";
    public static final String GLOBAL_ID_FRAGMENT = "globalId.fragment";
    public static final String GLOBAL_ID_VALUE_OBJECT = "globalId.valueObject";
    public static final String CHANGED_PROPERTIES = "changedProperties";


    private DB mongo;
    private JsonConverter jsonConverter;

    public MongoRepository(DB mongo) {
        this.mongo = mongo;
    }

    MongoRepository(DB mongo, JsonConverter jsonConverter) {
        this.mongo = mongo;
        this.jsonConverter = jsonConverter;
    }

    @Override
    public void persist(Commit commit) {
        persistSnapshots(commit);
        persistHeadId(commit);
    }

    void clean(){
        snapshotsCollection().remove(new BasicDBObject());
        headCollection().remove(new BasicDBObject());
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, int limit) {
        return queryForSnapshots(createIdQuery(globalId), limit);
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        return getLatest(createIdQuery(globalId));
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(Entity ownerEntity, String path, int limit) {
        BasicDBObject query = new BasicDBObject(GLOBAL_ID_OWNER_ID_ENTITY, ownerEntity.getClientsClass().getName());
        query.append(GLOBAL_ID_FRAGMENT, path);

        return queryForSnapshots(query, limit);
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(GlobalId globalId, String propertyName, int limit) {
        BasicDBObject query = createIdQuery(globalId);

        query.append(CHANGED_PROPERTIES, propertyName);

        return queryForSnapshots(query, limit);
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(ManagedClass givenClass, String propertyName, int limit) {
        BasicDBObject query = createGlobalIdClassQuery(givenClass);

        query.append(CHANGED_PROPERTIES, propertyName);

        return queryForSnapshots(query, limit);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(ManagedClass givenClass, int limit) {
        BasicDBObject query = createGlobalIdClassQuery(givenClass);
        return queryForSnapshots(query, limit);
    }

    @Override
    public CommitId getHeadId() {
        DBObject headId = headCollection().findOne();

        if (headId == null) {
            return null;
        }

        return jsonConverter.fromJson(headId.get(MongoHeadId.KEY).toString(), CommitId.class);
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Override
    public void ensureSchema() {
        //ensures collections and indexes
        DBCollection snapshots = snapshotsCollection();
        snapshots.createIndex(new BasicDBObject(GLOBAL_ID_KEY, ASC));
        snapshots.createIndex(new BasicDBObject(GLOBAL_ID_ENTITY, ASC));
        snapshots.createIndex(new BasicDBObject(GLOBAL_ID_VALUE_OBJECT, ASC));
        snapshots.createIndex(new BasicDBObject(GLOBAL_ID_OWNER_ID_ENTITY, ASC));
        snapshots.createIndex(new BasicDBObject(CHANGED_PROPERTIES, ASC));
        headCollection();

        //schema migration script from 1.1 to 1.2
        BSONObject doc = snapshots.findOne();
        if (doc != null) {
            Object stringCommitId = ((Map)doc.get("commitMetadata")).get("id");
            if (stringCommitId instanceof String) {
                mongo.eval("db.jv_snapshots.find().forEach(function(snapshot){snapshot.commitMetadata.id = Number(snapshot.commitMetadata.id);db.jv_snapshots.save(snapshot);});");
            }
        }
    }

    private BasicDBObject createIdQuery(GlobalId id) {
        return new BasicDBObject(GLOBAL_ID_KEY, id.value());
    }

    private BasicDBObject createGlobalIdClassQuery(ManagedClass givenClass) {
        String cName = givenClass.getClientsClass().getName();

        BasicDBObject query = null;
        if (givenClass instanceof Entity) {
            query = new BasicDBObject(GLOBAL_ID_ENTITY, cName);
        }
        if (givenClass instanceof ValueObject) {
            query = new BasicDBObject(GLOBAL_ID_VALUE_OBJECT, cName);
        }
        return query;
    }

    private CdoSnapshot readFromDBObject(DBObject dbObject) {
        return jsonConverter.fromJson(dbObject.toString(), CdoSnapshot.class);
    }

    private DBObject writeToDBObject(CdoSnapshot snapshot){
        conditionFulfilled(jsonConverter != null, "MongoRepository: jsonConverter is null");
        BasicDBObject dbObject = (BasicDBObject) JSON.parse(jsonConverter.toJson(snapshot));
        dbObject.append(GLOBAL_ID_KEY,snapshot.getGlobalId().value());
        return dbObject;
    }

    private DBCollection snapshotsCollection() {
        return mongo.getCollection(SNAPSHOTS);
    }

    private DBCollection headCollection() {
        return mongo.getCollection(MongoHeadId.COLLECTION_NAME);
    }

    private void persistSnapshots(Commit commit) {
        DBCollection collection = snapshotsCollection();
        for (CdoSnapshot snapshot: commit.getSnapshots()) {

            collection.save(writeToDBObject(snapshot));
        }
    }

    private void persistHeadId(Commit commit) {
        DBCollection headIdCollection = headCollection();

        DBObject oldHeadId = headIdCollection.findOne();
        MongoHeadId newHeadId = new MongoHeadId(jsonConverter.toJson(commit.getId()));

        if (oldHeadId == null) {
            headIdCollection.save(newHeadId);
        } else {
            headIdCollection.findAndModify(oldHeadId, newHeadId);
        }
    }

    private DBCursor getMongoSnapshotsCoursor(DBObject idQuery, int limit) {
        return snapshotsCollection()
                .find(idQuery).sort(new BasicDBObject(COMMIT_ID, DESC)).limit(limit);
    }

    private Optional<CdoSnapshot> getLatest(DBObject idQuery) {

        DBCursor mongoLatest = getMongoSnapshotsCoursor(idQuery, 1);

        if (mongoLatest.size() == 0) {
            return Optional.empty();
        }

        DBObject dbObject = mongoLatest.iterator().next();
        return Optional.of(readFromDBObject(dbObject));
    }

    private List<CdoSnapshot> queryForSnapshots(DBObject query, int limit) {

        DBCursor mongoSnapshots = getMongoSnapshotsCoursor(query, limit);

        Iterator<DBObject> iterator = mongoSnapshots.iterator();
        List<CdoSnapshot> snapshots = new ArrayList<>();

        while (iterator.hasNext()) {
            DBObject dbObject = iterator.next();
            snapshots.add(readFromDBObject(dbObject));
        }

        return snapshots;
    }
}