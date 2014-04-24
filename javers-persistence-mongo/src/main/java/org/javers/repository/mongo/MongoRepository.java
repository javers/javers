package org.javers.repository.mongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.javers.common.collections.Optional;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.repository.api.JaversRepository;

import java.util.List;

public class MongoRepository implements JaversRepository {

    public static final String COLLECTION_NAME = "Commit";

    private DB mongo;
    private CommitMapper commitMapper;

    public MongoRepository(DB mongo) {
        this.mongo = mongo;
        commitMapper = new CommitMapper();
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalCdoId globalId, int limit) {
        return null;
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalCdoId globalId) {
        return Optional.empty();
    }

    @Override
    public void persist(Commit commit) {
        DBCollection commits = mongo.getCollection(COLLECTION_NAME);

        DBObject commitAsDbObject = commitMapper.map(commit);

        commits.insert(commitAsDbObject);
    }

    @Override
    public CommitId getHeadId() {
        return null;
    }
}
