package org.javers.repository.api;

import org.javers.common.validation.Validate;
import org.joda.time.LocalDateTime;

/**
 * @author michal wesolowski
 */
public class QueryParamsBuilder {
    private int limit;
    private int skip;
    private LocalDateTime from;
    private LocalDateTime to;

    private QueryParamsBuilder(int limit) {
        this.limit = limit;
        this.skip = 0;
    };

    /**
     * Initializes builder with a given limit - number of snapshots to be fetched from database.
     * <br/>
     *
     * Always choose the reasonable limit,
     * production database could contain more records than you expect
     */
    public static QueryParamsBuilder withLimit(int limit) {
        checkLimit(limit);
        return new QueryParamsBuilder(limit);
    }

    /**
     * Initializes builder with parameters from a given queryParams instance
     */
    public static QueryParamsBuilder initializeWith(QueryParams queryParams) {
        Validate.argumentIsNotNull(queryParams);

        QueryParamsBuilder builder = QueryParamsBuilder.withLimit(queryParams.limit());
        if (queryParams.from().isPresent()) {
            builder = builder.from(queryParams.from().get());
        }
        if (queryParams.to().isPresent()) {
            builder = builder.to(queryParams.to().get());
        }
        return builder;
    }

    /**
     * @see #withLimit(int)
     */
    public QueryParamsBuilder limit(int limit) {
        checkLimit(limit);
        this.limit = limit;
        return this;
    }

    /**
     * skips a given number of the latest snapshots
     */
    public QueryParamsBuilder skip(int skip) {
        Validate.argumentCheck(limit >= 0, "Skip is not a non-negative number.");
        this.skip = skip;
        return this;
    }

    /**
     * limits results to Snapshots created after given date
     */
    public QueryParamsBuilder from(LocalDateTime from) {
        this.from = from;
        return this;
    }

    /**
     * limits results to Snapshots created before given date
     */
    public QueryParamsBuilder to(LocalDateTime to) {
        this.to = to;
        return this;
    }

    private static void checkLimit(int limit) {
        Validate.argumentCheck(limit > 0, "Limit is not a positive number.");
    }

    public QueryParams build() {
        return new QueryParams(limit, skip, from, to);
    }
}