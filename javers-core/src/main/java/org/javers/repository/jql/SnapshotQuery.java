package org.javers.repository.jql;

import org.javers.core.metamodel.object.CdoSnapshot;

import java.util.List;

/**
 * Created by bartosz.walacik on 2015-03-28.
 */
public class SnapshotQuery extends Query<CdoSnapshot>{
    public SnapshotQuery(Class from, List<Filter> filters, int limit) {
        super(from, filters, limit);
    }
}
