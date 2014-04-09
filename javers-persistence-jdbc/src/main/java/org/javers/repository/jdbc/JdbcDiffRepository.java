package org.javers.repository.jdbc;

import org.javers.core.commit.Commit;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.repository.api.JaversRepository;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class JdbcDiffRepository implements JaversRepository {
    @Override
    public CdoSnapshot getLatest(GlobalCdoId globalId) {
        return null;
    }

    @Override
    public void persist(Commit commit) {

    }
}
