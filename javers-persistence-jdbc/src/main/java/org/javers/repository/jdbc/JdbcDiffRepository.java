package org.javers.repository.jdbc;

import org.javers.common.collections.Optional;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.repository.api.JaversRepository;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class JdbcDiffRepository implements JaversRepository {

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalCdoId globalId, int limit) {
        return null;
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalCdoId globalId) {
        return null;
    }

    @Override
    public void persist(Commit commit) {

    }

    @Override
    public CommitId getHeadId() {
        return null;
    }
}
