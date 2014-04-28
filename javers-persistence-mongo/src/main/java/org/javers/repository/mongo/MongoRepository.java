package org.javers.repository.mongo;

import com.mongodb.BasicDBObjectBuilder;
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
import org.javers.repository.api.JaversRepository;

import java.util.ArrayList;
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
        DBCursor cursor = getSnapshots(globalId, limit);

        List<CdoSnapshot> snapshots = new ArrayList<>();

        while (cursor.hasNext()) {
            snapshots.add(commitMapper.toCdoSnapshot(cursor.next()));
        }

        return snapshots;
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalCdoId globalId) {
        DBCursor cursor = getSnapshots(globalId, 1);

        if (cursor.hasNext()) {
            CdoSnapshot cdoSnapshot = commitMapper.toCdoSnapshot(cursor.next());
            return Optional.of(cdoSnapshot);
        }

        return Optional.empty();
    }

    @Override
    public void persist(Commit commit) {
        DBCollection commits = mongo.getCollection(COLLECTION_NAME);

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

    private DBCursor getSnapshots(GlobalCdoId globalCdoId, int limit) {
        return mongo.getCollection(COLLECTION_NAME).find()
                .sort(BasicDBObjectBuilder.start().add("id.majorId", -1).get()).limit(limit);
    }
}
