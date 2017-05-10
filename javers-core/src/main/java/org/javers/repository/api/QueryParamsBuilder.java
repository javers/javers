package org.javers.repository.api;

import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitId;
import java.time.LocalDateTime;

import java.util.*;

/**
 * @author michal wesolowski
 */
public class QueryParamsBuilder {
    private int limit;
    private int skip;
    private LocalDateTime from;
    private LocalDateTime to;
    private Set<CommitId> commitIds = new HashSet<>();
    private Long version;
    private String author;
    private boolean aggregate;
    private boolean newObjectChanges;
    private Map<String, String> commitProperties = new HashMap<>();
    private String changedProperty;

    private QueryParamsBuilder(int limit) {
        this.limit = limit;
        this.skip = 0;
    };

    /**
     * Initializes builder with a given limit - number of snapshots to be fetched from database.
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
        builder.commitIds(queryParams.commitIds());
        builder.skip(queryParams.skip());
        if (queryParams.from().isPresent()) {
            builder.from(queryParams.from().get());
        }
        if (queryParams.to().isPresent()) {
            builder.to(queryParams.to().get());
        }
        if (queryParams.version().isPresent()) {
            builder.version(queryParams.version().get());
        }
        return builder;
    }

    public QueryParamsBuilder withChildValueObjects(boolean aggregate) {
        this.aggregate = aggregate;
        return this;
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
     * @see QueryParams#skip()
     */
    public QueryParamsBuilder skip(int skip) {
        Validate.argumentCheck(limit >= 0, "Skip is not a non-negative number.");
        this.skip = skip;
        return this;
    }

    /**
     * @see QueryParams#from()
     */
    public QueryParamsBuilder from(LocalDateTime from) {
        this.from = from;
        return this;
    }

    /**
     * @see QueryParams#to()
     */
    public QueryParamsBuilder to(LocalDateTime to) {
        this.to = to;
        return this;
    }

    /**
     * @see QueryParams#commitIds()
     */
    public QueryParamsBuilder commitId(CommitId commitId) {
        this.commitIds.add(commitId);
        return this;
    }

    /**
     * @see QueryParams#commitIds()
     */
    public QueryParamsBuilder commitIds(Collection<CommitId> commitIds) {
        this.commitIds.addAll(commitIds);
        return this;
    }

    /**
     * @see QueryParams#commitProperties()
     */
    public QueryParamsBuilder commitProperty(String name, String value) {
        this.commitProperties.put(name, value);
        return this;
    }

    /**
     * @see QueryParams#version()
     */
    public QueryParamsBuilder version(Long version) {
        this.version = version;
        return this;
    }


    public QueryParamsBuilder newObjectChanges(boolean newObjectChanges) {
        this.newObjectChanges = newObjectChanges;
        return this;
    }

    public QueryParamsBuilder changedProperty(String propertyName) {
        this.changedProperty = propertyName;
        return this;
    }

    /**
     * @see QueryParams#author()
     */
    public QueryParamsBuilder author(String author) {
        this.author = author;
        return this;
    }

    private static void checkLimit(int limit) {
        Validate.argumentCheck(limit > 0, "Limit is not a positive number.");
    }

    public QueryParams build() {
        return new QueryParams(limit, skip, from, to, commitIds, version, author, commitProperties, aggregate, newObjectChanges, changedProperty);
    }
}