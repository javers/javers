package org.javers.repository.jdbc;

import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.repository.api.DiffRepository;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class JdbcDiffRepository implements DiffRepository{
    @Override
    public void save(Diff newDiff) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Diff getById(long diffId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Change> findByGlobalCdoId(GlobalCdoId globalCdoId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public CdoSnapshot getLatest(GlobalCdoId objectId) {
        return null;
    }
}
