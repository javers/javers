package org.javers.repository.mongo;

import com.mongodb.*;
import com.mongodb.util.JSON;
import org.javers.common.collections.Optional;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.GlobalIdDTO;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.mongo.model.MongoHeadId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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


    private DB mongo;
    private JsonConverter jsonConverter;

    public MongoRepository(DB mongo) {
        this.mongo = mongo;

        //ensures collections and indexes
        DBCollection snapshots = snapshotsCollection();
        snapshots.ensureIndex(new BasicDBObject(GLOBAL_ID_KEY,ASC),"global_id_idx");
        headCollection();
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

    void clean(){
        snapshotsCollection().remove(new BasicDBObject());
        headCollection().remove(new BasicDBObject());
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, int limit) {
        return getStateHistory(createIdQuery(globalId), limit);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalIdDTO globalIdDTO, int limit) {
        return getStateHistory(createIdQuery(globalIdDTO), limit);
    }

    private List<CdoSnapshot> getStateHistory(DBObject cdoId, int limit) {

        DBCursor mongoSnapshots = getMongoSnapshotsCoursor(cdoId, limit);

        Iterator<DBObject> iterator = mongoSnapshots.iterator();
        List<CdoSnapshot> snapshots = new ArrayList<>();

        while (iterator.hasNext()) {
            DBObject dbObject = iterator.next();
            snapshots.add(readFromDBObject(dbObject));
        }

        return snapshots;
    }

    private DBCursor getMongoSnapshotsCoursor(DBObject idQuery, int limit) {
        return snapshotsCollection()
              .find(idQuery).sort(new BasicDBObject(COMMIT_ID, DESC)).limit(limit);
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        return getLatest(createIdQuery(globalId));
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalIdDTO globalIdDTO) {
        return getLatest(createIdQuery(globalIdDTO));
    }

    private Optional<CdoSnapshot> getLatest(DBObject idQuery) {

        DBCursor mongoLatest = getMongoSnapshotsCoursor(idQuery, 1);

        if (mongoLatest.size() == 0) {
            return Optional.empty();
        }

        DBObject dbObject = mongoLatest.iterator().next();
        return Optional.of(readFromDBObject(dbObject));
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

    private BasicDBObject createIdQuery(GlobalId id) {
        return new BasicDBObject(GLOBAL_ID_KEY, id.value());
    }

    private BasicDBObject createIdQuery(GlobalIdDTO id) {
        return new BasicDBObject(GLOBAL_ID_KEY,  id.value());
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
}