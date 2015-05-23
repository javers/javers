package org.javers.repository.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
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


    private MongoDatabase mongo;
    private JsonConverter jsonConverter;

    public MongoRepository(MongoDatabase mongo) {
        this.mongo = mongo;
    }

    MongoRepository(MongoDatabase mongo, JsonConverter jsonConverter) {
        this.mongo = mongo;
        this.jsonConverter = jsonConverter;
    }

    @Override
    public void persist(Commit commit) {
        persistSnapshots(commit);
        persistHeadId(commit);
    }

    void clean(){
        snapshotsCollection().deleteMany(new Document());
        headCollection().deleteMany(new Document());
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
        Document headId = headCollection().find().first();

        if (headId == null) {
            return null;
        }

        return new MongoHeadId(headId).toCommitId();
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Override
    public void ensureSchema() {
        //ensures collections and indexes
        MongoCollection<Document> snapshots = snapshotsCollection();
        snapshots.createIndex(new BasicDBObject(GLOBAL_ID_KEY, ASC));
        snapshots.createIndex(new BasicDBObject(GLOBAL_ID_ENTITY, ASC));
        snapshots.createIndex(new BasicDBObject(GLOBAL_ID_VALUE_OBJECT, ASC));
        snapshots.createIndex(new BasicDBObject(GLOBAL_ID_OWNER_ID_ENTITY, ASC));
        snapshots.createIndex(new BasicDBObject(CHANGED_PROPERTIES, ASC));
        headCollection();

        //schema migration script from JaVers 1.1 to 1.2
        Document doc = snapshots.find().first();
        if (doc != null) {
            Object stringCommitId = ((Map)doc.get("commitMetadata")).get("id");
            if (stringCommitId instanceof String) {
                DBObject updateCmd = new BasicDBObject();

                //TODO
              //  mongo.runCommand("db.jv_snapshots.find().forEach(function(snapshot){snapshot.commitMetadata.id = Number(snapshot.commitMetadata.id);db.jv_snapshots.save(snapshot);});");
            }
        }
    }

    private BasicDBObject createIdQuery(GlobalId id) {
        return new BasicDBObject (GLOBAL_ID_KEY, id.value());
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

    private CdoSnapshot readFromDBObject(Document dbObject) {
        return jsonConverter.fromJson(dbObject.toJson(), CdoSnapshot.class);
    }

    private Document writeToDBObject(CdoSnapshot snapshot){
        conditionFulfilled(jsonConverter != null, "MongoRepository: jsonConverter is null");
        Document dbObject = Document.parse(jsonConverter.toJson(snapshot));
        dbObject.append(GLOBAL_ID_KEY,snapshot.getGlobalId().value());
        return dbObject;
    }

    private MongoCollection<Document> snapshotsCollection() {
        return mongo.getCollection(SNAPSHOTS);
    }

    private MongoCollection<Document> headCollection() {
        return mongo.getCollection(MongoHeadId.COLLECTION_NAME);
    }

    private void persistSnapshots(Commit commit) {
        MongoCollection collection = snapshotsCollection();
        for (CdoSnapshot snapshot: commit.getSnapshots()) {

            collection.insertOne(writeToDBObject(snapshot));
        }
    }

    private void persistHeadId(Commit commit) {
        MongoCollection<Document> headIdCollection = headCollection();

        Document oldHead = headIdCollection.find().first();
        MongoHeadId newHeadId = new MongoHeadId(commit.getId());

        if (oldHead == null) {
            headIdCollection.insertOne(newHeadId.toDocument());
        } else {
            headIdCollection.findOneAndUpdate(oldHead, newHeadId.toDocument());
        }
    }

    private MongoCursor getMongoSnapshotsCoursor(Bson idQuery, int limit) {
        return snapshotsCollection()
                .find(idQuery).sort(new Document(COMMIT_ID, DESC)).limit(limit).iterator();
    }

    private Optional<CdoSnapshot> getLatest(Bson idQuery) {

        MongoCursor mongoLatest = getMongoSnapshotsCoursor(idQuery, 1);

        if (!mongoLatest.hasNext()) {
            return Optional.empty();
        }

        Document dbObject = getOne(mongoLatest);
        return Optional.of(readFromDBObject(dbObject));
    }

    private List<CdoSnapshot> queryForSnapshots(Bson query, int limit) {

        MongoCursor<Document> mongoSnapshots = getMongoSnapshotsCoursor(query, limit);

        List<CdoSnapshot> snapshots = new ArrayList<>();
        try {
            while (mongoSnapshots.hasNext()) {
                Document dbObject = mongoSnapshots.next();
                snapshots.add(readFromDBObject(dbObject));
            }
            return snapshots;
        }
        finally {
            mongoSnapshots.close();
        }
    }

    private <T> T getOne(MongoCursor mongoCursor){
        try{
            return (T)mongoCursor.next();
        }
        finally {
            mongoCursor.close();
        }
    }
}