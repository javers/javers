package org.javers.repository.jql;

import org.javers.core.diff.Change;

import java.util.List;

/**
 * Created by bartosz.walacik on 2015-03-28.
 */
public class ChangeQuery extends Query<Change> {
    public ChangeQuery(Class from, List<Filter> filters, int limit) {
        super(from, filters, limit);
    }
}
