package org.javers.repository.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.javers.common.collections.Optional;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.repository.api.JaversRepository;

import java.util.Collections;
import java.util.List;

public class MongoRepository implements JaversRepository {

    private String collectionName = "Commit";

    private DB mongo;
    private Mapper mapper;

    public MongoRepository(DB mongo) {
        this.mongo = mongo;
    }

    public MongoRepository(DB mongo, JsonConverter jsonConverter) {
        this.mongo = mongo;
        this.mapper = new Mapper(jsonConverter);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalCdoId globalId, int limit) {
        throw new UnsupportedOperationException("use: Optional<CdoSnapshot> getLatest(InstanceId.InstanceIdDTO dtoId)");
    }

    @Override
    public List<CdoSnapshot> getStateHistory(InstanceId.InstanceIdDTO dtoId, int limit) {
        DBObject globalIdAsDBObject = mapper.toDBObject(dtoId);
        DBCursor commit = mongo.getCollection(collectionName).find(globalIdAsDBObject);

        if (commit == null) {
            return Collections.EMPTY_LIST;
        }

        return mapper.toCdoSnapshots(commit);
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalCdoId globalId) {
        DBObject dbObject = new BasicDBObject(Mapper.GLOBAL_CDO_ID, mapper.toDBObject(globalId));

        DBCursor commit = mongo.getCollection(collectionName).find(dbObject)
                .sort(new BasicDBObject("date", 1)).limit(1);

        if (commit.length() != 1) {
            throw new RuntimeException("Cos nie tak");
        }

        CdoSnapshot snapshot = mapper.toCdoSnapshot(commit.iterator().next());

        return Optional.fromNullable(snapshot);
    }

    @Override
    public Optional<CdoSnapshot> getLatest(InstanceId.InstanceIdDTO dtoId) {
        DBObject dbObject = mapper.toDBObject(dtoId);
        DBObject commit = mongo.getCollection(collectionName).findOne(dbObject);
        CdoSnapshot snapshot = mapper.toCdoSnapshot(commit);

        return Optional.fromNullable(snapshot);
    }

    @Override
    public void persist(Commit commit) {
        DBCollection commits = mongo.getCollection(collectionName);

        DBObject commitAsDbObject = mapper.toDBObject(commit);

        commits.insert(commitAsDbObject);
    }

    @Override
    public CommitId getHeadId() {
        return null;
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
        if (mapper == null) {
            mapper = new Mapper(jsonConverter);
        } else {
            mapper.setJsonConverter(jsonConverter);
        }
    }
}
