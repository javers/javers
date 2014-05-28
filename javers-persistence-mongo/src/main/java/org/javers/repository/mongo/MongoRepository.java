package org.javers.repository.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
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
import org.javers.core.metamodel.object.InstanceId;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.mongo.model.MongoHeadId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author pawel szymczyk
 */
public class MongoRepository implements JaversRepository {

    private DB mongo;
    private ModelMapper mapper;
    private JsonConverter jsonConverter;

    public MongoRepository(DB mongo) {
        this.mongo = mongo;
    }

    public MongoRepository(DB mongo, JsonConverter jsonConverter) {
        this.mongo = mongo;
        this.mapper = new ModelMapper(jsonConverter);
        this.jsonConverter = jsonConverter;
    }

    @Override
    public void persist(Commit commit) {
        persistSnapshots(commit);
        persistHeadId(commit);
    }

    private void persistSnapshots(Commit commit) {

        DBCollection collection = mongo.getCollection("Snapshots");

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
    public List<CdoSnapshot> getStateHistory(InstanceId.InstanceIdDTO dtoId, int limit) {
        return getStateHistory(toDBObject(dtoId), limit);
    }

    private List<CdoSnapshot> getStateHistory(BasicDBObject id, int limit) {

        DBCursor mongoSnapshots = mongo.getCollection("Snapshots")
                .find(id).sort(new BasicDBObject("commitId", 1)).limit(limit);

        Iterator<DBObject> iterator = mongoSnapshots.iterator();
        List<CdoSnapshot> snapshots = new ArrayList<>();

        while (iterator.hasNext()) {
            snapshots.add(mapper.toCdoSnapshot(iterator.next()));
        }

        return snapshots;
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalCdoId globalId) {
        return getLatest(toDBObject(globalId));
    }

    @Override
    public Optional<CdoSnapshot> getLatest(InstanceId.InstanceIdDTO dtoId) {
        return getLatest(toDBObject(dtoId));
    }

    private Optional<CdoSnapshot> getLatest(DBObject id) {

        DBCursor mongoLatest = mongo.getCollection("Snapshots")
                .find(id).sort(new BasicDBObject("commitId", 1)).limit(1);

        if (mongoLatest.size() == 0) {
            return Optional.empty();
        }

        return Optional.of(mapper.toCdoSnapshot(mongoLatest.iterator().next()));
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
        this.mapper = new ModelMapper(jsonConverter);
        this.jsonConverter = jsonConverter;
    }

    private BasicDBObject toDBObject(GlobalCdoId id) {
        return new BasicDBObject("globalCdoId", JSON.parse(jsonConverter.toJson(id)));
    }

    private BasicDBObject toDBObject(InstanceId.InstanceIdDTO id) {

        //TODO there is no InstanceIdDTO JSON type adapter
        return new BasicDBObject("globalCdoId", BasicDBObjectBuilder.start()
                .add("cdoId", id.getCdoId())
                .add("entity", id.getEntity().getName())
                .get()
        );
    }
}
