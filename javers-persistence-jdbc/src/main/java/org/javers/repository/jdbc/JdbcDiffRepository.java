package org.javers.repository.jdbc;

import org.javers.common.collections.Optional;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.*;
import org.javers.repository.api.JaversRepository;

import java.util.List;

/**
 * @author bartosz walacik
 */
@Deprecated
public class JdbcDiffRepository implements JaversRepository {

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, int limit) {
        return null;
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalIdDTO globalIdDTO, int limit) {
        return null;
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        return null;
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalIdDTO globalIdDTO) {
        return null;
    }

    @Override
    public void persist(Commit commit) {

    }

    @Override
    public CommitId getHeadId() {
        return null;
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {

    }
}
