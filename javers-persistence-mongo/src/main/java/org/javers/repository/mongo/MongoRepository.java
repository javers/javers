package org.javers.repository.mongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.javers.common.collections.Optional;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.repository.api.JaversRepository;

import java.util.Collections;
import java.util.List;

public class MongoRepository implements JaversRepository {

    private String collectionName = "Commit";

    private DB mongo;
    private CommitMapper commitMapper;

    public MongoRepository(DB mongo, CommitMapper commitMapper) {
        this.mongo = mongo;
        this.commitMapper = commitMapper;
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalCdoId globalId, int limit) {
        DBObject globalIdAsDBObject = commitMapper.toDBObject(globalId);
        DBObject commit = mongo.getCollection(collectionName).findOne(globalIdAsDBObject);

        if (commit == null) {
            return Collections.EMPTY_LIST;
        }

        return commitMapper.toCdoSnapshots(commit);
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalCdoId globalId) {
        DBObject dbObject = commitMapper.toDBObject(globalId);
        DBObject commit = mongo.getCollection(collectionName).findOne(dbObject);
        List<CdoSnapshot> snapshots = commitMapper.toCdoSnapshots(commit);

        return Optional.fromNullable(snapshots.get(snapshots.size() - 1));
    }

    @Override
    public void persist(Commit commit) {
        DBCollection commits = mongo.getCollection(collectionName);

        DBObject commitAsDbObject = commitMapper.toDBObject(commit);

        commits.insert(commitAsDbObject);
    }

    @Override
    public CommitId getHeadId() {
        return null;
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
        commitMapper.setJsonConverter(jsonConverter);
    }
}
