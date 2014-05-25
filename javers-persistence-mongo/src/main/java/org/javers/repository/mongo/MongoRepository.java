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
import org.javers.repository.mongo.model.MongoCdoSnapshots;
import org.javers.repository.mongo.model.MongoChange;
import org.javers.repository.mongo.model.MongoCommit;
import org.javers.repository.mongo.model.MongoHeadId;
import org.javers.repository.mongo.model.MongoSnapshot;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class MongoRepository implements JaversRepository {

    private DB mongo;
    private ModelMapper2 mapper;
    private JsonConverter jsonConverter;

    public MongoRepository(DB mongo) {
        this.mongo = mongo;
    }

    public MongoRepository(DB mongo, JsonConverter jsonConverter) {
        this.mongo = mongo;
        this.mapper = new ModelMapper2(jsonConverter);
        this.jsonConverter = jsonConverter;
    }

    @Override
    public void persist(Commit commit) {
        persistCommit(commit);
        persistSnapshots(commit);
        persistChanges(commit);
        persistHeadId(commit);
    }

    private void persistCommit(Commit commit) {
        MongoCommit mongoCommit = mapper.toMongoCommit(commit);

//        mongo.getCollection(MongoCommit.COLLECTION_NAME)
//                .save(mongoCommit);
    }

    private void persistSnapshots(Commit commit) {

        DBCollection collection = mongo.getCollection(MongoCdoSnapshots.COLLECTION_NAME);

        DBObject mongoCdoSnapshots = collection
                .findOne(new BasicDBObject(MongoCdoSnapshots.GLOBAL_CDO_ID,
                        jsonConverter.toJson(commit.getGlobalCdoId())));

        if (mongoCdoSnapshots == null) {
            collection.save(mapper.toMongoSnaphot(commit));
        } else {
            MongoCdoSnapshots snapshots = new MongoCdoSnapshots(mongoCdoSnapshots);

            for (CdoSnapshot snapshot: commit.getSnapshots()) {
                snapshots.addSnapshot(new MongoSnapshot((DBObject) JSON.parse(jsonConverter.toJson(snapshot))));
            }

            collection.findAndModify(mongoCdoSnapshots, snapshots);
        }

    }

    private void persistChanges(Commit commit) {
        MongoChange mongoChange = mapper.toMongoChange(commit);

//        mongo.getCollection(MongoChange.COLLECTION_NAME)
//                .save(mongoChange);

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
        throw new NotImplementedException();
//        return getStateHistory(new BasicDBObject("globalCdoId", mapper.toDBObject(globalId)), limit);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(InstanceId.InstanceIdDTO dtoId, int limit) {
        throw new NotImplementedException();
//        return getStateHistory(new BasicDBObject("globalCdoId", mapper.toDBObject(dtoId)), limit);
    }

    public List<CdoSnapshot> getStateHistory(DBObject id, int limit) {
//        DBCursor commit = mongo.getCollection(collectionName).find(id);
//
//        if (commit.length() == 0) {
//            return Collections.EMPTY_LIST;
//        }
//
//        List<CdoSnapshot> snapshots = new ArrayList<>();
//        Iterator<DBObject> iterator = commit.iterator();
//
//        int i = 0;
//        while (iterator.hasNext() && i<= limit) {
//            snapshots.addAll(mapper.toCdoSnapshots((BasicDBList) iterator.next().get("snapshots")));
//            i++;
//        }
//
//        return snapshots;
        return null;
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalCdoId globalId) {
        return getLatest((DBObject) JSON.parse(jsonConverter.toJson(globalId)));
    }

    @Override
    public Optional<CdoSnapshot> getLatest(InstanceId.InstanceIdDTO dtoId) {

        DBObject dbObject = new BasicDBObject("globalCdoId", BasicDBObjectBuilder.start()
                .append("cdoId", dtoId.getCdoId())
                .append("entity", dtoId.getEntity().getName()).get());

        return getLatest(dbObject);
    }

    private Optional<CdoSnapshot> getLatest(DBObject id) {

        DBObject mongoCdoSnapshots = mongo.getCollection(MongoCdoSnapshots.COLLECTION_NAME)
                .findOne(id);

        if (mongoCdoSnapshots == null) {
            return Optional.empty();
        }

        MongoCdoSnapshots cdoSnapshots = new MongoCdoSnapshots(mongoCdoSnapshots);

        MongoSnapshot latest = cdoSnapshots.getLatest();

        return Optional.of(mapper.toCdoSnapshot(latest, cdoSnapshots.getGlobalCdoId()));
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
}
