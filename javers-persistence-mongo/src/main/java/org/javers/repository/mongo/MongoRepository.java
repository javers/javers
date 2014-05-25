package org.javers.repository.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.javers.common.collections.Function;
import org.javers.common.collections.Lists;
import org.javers.common.collections.Optional;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.mongo.model.MongoCdoSnapshots;
import org.javers.repository.mongo.model.MongoHeadId;
import org.javers.repository.mongo.model.MongoSnapshot;

import java.util.Collections;
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

        DBCollection collection = mongo.getCollection(MongoCdoSnapshots.COLLECTION_NAME);

        for (CdoSnapshot snapshot: commit.getSnapshots()) {

            DBObject globalCdoId = toDBObject(snapshot.getGlobalId());

            DBObject mongoCdoSnapshots = collection
                    .findOne(globalCdoId);

            if (mongoCdoSnapshots == null) {
                collection.save(mapper.toMongoCdoSnapshot(snapshot));
            } else {
                MongoCdoSnapshots snapshots = new MongoCdoSnapshots(mongoCdoSnapshots);
                snapshots.addSnapshot(new MongoSnapshot((DBObject) JSON.parse(jsonConverter.toJson(snapshot))));
                collection.findAndModify(globalCdoId, snapshots);
            }
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

    private List<CdoSnapshot> getStateHistory(DBObject id, int limit) {

        DBObject mongoCdoSnapshots = mongo.getCollection(MongoCdoSnapshots.COLLECTION_NAME)
                .findOne(id);

        if (mongoCdoSnapshots == null) {
            return Collections.EMPTY_LIST;
        }

        final MongoCdoSnapshots cdoSnapshots = new MongoCdoSnapshots(mongoCdoSnapshots);

        return Lists.transform(cdoSnapshots.getLatest(limit),
                new Function<MongoSnapshot, CdoSnapshot>() {
                    @Override
                    public CdoSnapshot apply(MongoSnapshot input) {
                        return mapper.toCdoSnapshot(input, cdoSnapshots.getGlobalCdoId());
                    }
                });
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

        DBObject mongoCdoSnapshots = mongo.getCollection(MongoCdoSnapshots.COLLECTION_NAME).findOne(id);

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
        this.mapper = new ModelMapper(jsonConverter);
        this.jsonConverter = jsonConverter;
    }

    private DBObject toDBObject(GlobalCdoId id) {
        return new BasicDBObject(MongoCdoSnapshots.GLOBAL_CDO_ID, JSON.parse(jsonConverter.toJson(id)));
    }

    private DBObject toDBObject(InstanceId.InstanceIdDTO id) {
        return new BasicDBObject(MongoCdoSnapshots.GLOBAL_CDO_ID, JSON.parse(jsonConverter.toJson(id)));
    }
}
