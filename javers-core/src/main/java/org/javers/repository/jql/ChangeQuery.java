package org.javers.repository.jql;

import org.javers.core.diff.Change;

import java.util.List;

/**
 * Created by bartosz.walacik on 2015-03-28.
 */
public class ChangeQuery extends Query<Change> {
    ChangeQuery(List<Filter> filters, int limit) {
        super(filters, limit);
    }
}
