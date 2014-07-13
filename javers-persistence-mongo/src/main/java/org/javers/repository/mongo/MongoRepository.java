package org.javers.repository.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.javers.common.collections.Optional;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.GlobalIdDTO;
import org.javers.core.metamodel.object.InstanceIdDTO;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.mongo.model.MongoHeadId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author pawel szymczyk
 */
public class MongoRepository implements JaversRepository {
    private static final int DESC = -1;
    public static final String SNAPSHOTS = "snapshots";
    public static final String GLOBAL_CDO_ID = "globalCdoId";
    public static final String COMMIT_ID = "commitId";
    private DB mongo;
    private JsonConverter jsonConverter;

    public MongoRepository(DB mongo) {
        this.mongo = mongo;
    }

    public MongoRepository(DB mongo, JsonConverter jsonConverter) {
        this.mongo = mongo;
        this.jsonConverter = jsonConverter;
    }

    @Override
    public void persist(Commit commit) {
        persistSnapshots(commit);
        persistHeadId(commit);
    }

    private void persistSnapshots(Commit commit) {

        DBCollection collection = mongo.getCollection(SNAPSHOTS);

        for (CdoSnapshot snapshot: commit.getSnapshots()) {
            collection.save((DBObject) JSON.parse(jsonConverter.toJson(snapshot)));
        }
    }

    private void persistHeadId(Commit commit) {
        DBCollection headIdCollection = mongo.getCollection(MongoHeadId.COLLECTION_NAME);

        DBObject oldHeadId = headIdCollection.findOne();
        MongoHeadId newHeadId = new MongoHeadId(jsonConverter.toJson(commit.getId()));

        if (oldHeadId == null) {
            headIdCollection.save(newHeadId);
        } else {
            headIdCollection.findAndModify(oldHeadId, newHeadId);
        }
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalCdoId globalId, int limit) {
        return getStateHistory(toDBObject(globalId), limit);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalIdDTO globalIdDTO, int limit) {
        return getStateHistory(toDBObject(globalIdDTO), limit);
    }

    private List<CdoSnapshot> getStateHistory(DBObject cdoId, int limit) {

        DBCursor mongoSnapshots = getMongoSnapshotsCoursor(cdoId, limit);

        Iterator<DBObject> iterator = mongoSnapshots.iterator();
        List<CdoSnapshot> snapshots = new ArrayList<>();

        while (iterator.hasNext()) {
            DBObject dbObject = iterator.next();
            snapshots.add(fromDBObject(dbObject));
        }

        return snapshots;
    }

    private DBCursor getMongoSnapshotsCoursor(DBObject cdoId, int limit) {
        return mongo.getCollection(SNAPSHOTS)
                    .find(cdoId).sort(new BasicDBObject(COMMIT_ID, DESC)).limit(limit);
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalCdoId globalId) {
        return getLatest(toDBObject(globalId));
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalIdDTO globalIdDTO) {
        return getLatest(toDBObject(globalIdDTO));
    }

    private Optional<CdoSnapshot> getLatest(DBObject id) {

        DBCursor mongoLatest = getMongoSnapshotsCoursor(id, 1);

        if (mongoLatest.size() == 0) {
            return Optional.empty();
        }

        DBObject dbObject = mongoLatest.iterator().next();
        return Optional.of(fromDBObject(dbObject));
    }

    @Override
    public CommitId getHeadId() {
        DBObject headId = mongo.getCollection(MongoHeadId.COLLECTION_NAME).findOne();

        if (headId == null) {
            return null;
        }

        return jsonConverter.fromJson(headId.get(MongoHeadId.KEY).toString(), CommitId.class);
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    private BasicDBObject toDBObject(GlobalCdoId id) {

        System.out.println("toDBObject: "+ id.value()+ " : "+ jsonConverter.toJson(id));

        return new BasicDBObject(GLOBAL_CDO_ID, JSON.parse(jsonConverter.toJson(id)));
    }

    private BasicDBObject toDBObject(GlobalIdDTO id) {
        System.out.println("toDBObject: "+ id.value()+ " : "+ jsonConverter.toJson(id));

        return new BasicDBObject(GLOBAL_CDO_ID, JSON.parse(jsonConverter.toJson(id)));
    }

    private CdoSnapshot fromDBObject(DBObject dbObject) {
        return jsonConverter.fromJson(dbObject.toString(), CdoSnapshot.class);
    }
}