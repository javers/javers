package org.javers.repository.jql;

import org.javers.common.validation.Validate;

import java.util.List;

/**
 * Created by bartosz.walacik on 2015-03-28.
 */
public abstract class Query<T> {

    private final int limit;
    private final Class from;
    private final List<Filter> filters;

    Query (Class from, List<Filter> filters, int limit) {
        Validate.argumentsAreNotNull(from, filters);
        this.limit = limit;
        this.from = from;
        this.filters = filters;
    }

    /**
     * choose reasonable limit (number of objects to fetch),
     * production database could contain more records than you expect
     */
    public int getLimit() {
        return limit;
    }
}
