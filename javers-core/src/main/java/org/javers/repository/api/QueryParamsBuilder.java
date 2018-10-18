package org.javers.repository.api;

import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitId;
import org.javers.core.metamodel.object.SnapshotType;
import org.javers.repository.jql.QueryBuilder;

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
    private CommitId toCommitId;
    private Set<CommitId> commitIds = new HashSet<>();
    private Long version;
    private String author;
    private boolean aggregate;
    private boolean newObjectChanges;
    private Map<String, String> commitProperties = new HashMap<>();
    private String changedProperty;
    private SnapshotType snapshotType;
    private boolean loadCommitProps = true;

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
     * @see QueryBuilder#withChildValueObjects()
     */
    public QueryParamsBuilder withChildValueObjects(boolean aggregate) {
        this.aggregate = aggregate;
        return this;
    }

    /**
     * @param loadCommitProps true by default
     */
    public QueryParamsBuilder withCommitProps(boolean loadCommitProps) {
        this.loadCommitProps = loadCommitProps;
        return this;
    }

    /**
     * @see QueryBuilder#limit(int)
     */
    public QueryParamsBuilder limit(int limit) {
        checkLimit(limit);
        this.limit = limit;
        return this;
    }

    /**
     * @see QueryBuilder#skip(int)
     */
    public QueryParamsBuilder skip(int skip) {
        Validate.argumentCheck(limit >= 0, "Skip is not a non-negative number.");
        this.skip = skip;
        return this;
    }

    /**
     * @see QueryBuilder#from(LocalDateTime)
     */
    public QueryParamsBuilder from(LocalDateTime from) {
        this.from = from;
        return this;
    }

    /**
     * @see QueryBuilder#to(LocalDateTime)
     */
    public QueryParamsBuilder to(LocalDateTime to) {
        this.to = to;
        return this;
    }

    /**
     * @see QueryBuilder#withCommitId(CommitId)
     */
    public QueryParamsBuilder commitId(CommitId commitId) {
        this.commitIds.add(commitId);
        return this;
    }

    /**
     * @see QueryBuilder#toCommitId(CommitId)
     */
    public QueryParamsBuilder toCommitId(CommitId toCommitId) {
        this.toCommitId = toCommitId;
        return this;
    }

    /**
     * @see QueryBuilder#withCommitIds(Collection)
     */
    public QueryParamsBuilder commitIds(Collection<CommitId> commitIds) {
        this.commitIds.addAll(commitIds);
        return this;
    }

    /**
     * @see QueryBuilder#withCommitProperty(String, String)
     */
    public QueryParamsBuilder commitProperty(String name, String value) {
        this.commitProperties.put(name, value);
        return this;
    }

    /**
     * @see QueryBuilder#withVersion(long)
     */
    public QueryParamsBuilder version(Long version) {
        this.version = version;
        return this;
    }

    /**
     * @see QueryBuilder#withNewObjectChanges(boolean)
     */
    public QueryParamsBuilder newObjectChanges(boolean newObjectChanges) {
        this.newObjectChanges = newObjectChanges;
        return this;
    }

    /**
     * @see QueryBuilder#withSnapshotType(SnapshotType)
     */
    public QueryParamsBuilder withSnapshotType(SnapshotType snapshotType) {
        this.snapshotType = snapshotType;
        return this;
    }

    /**
     * @see QueryBuilder#withChangedProperty(String)
     */
    public QueryParamsBuilder changedProperty(String propertyName) {
        this.changedProperty = propertyName;
        return this;
    }

    /**
     * @see QueryBuilder#byAuthor(String)
     */
    public QueryParamsBuilder author(String author) {
        this.author = author;
        return this;
    }

    private static void checkLimit(int limit) {
        Validate.argumentCheck(limit > 0, "Limit is not a positive number.");
    }

    public QueryParams build() {
        return new QueryParams(limit, skip, from, to, commitIds, version, author, commitProperties, aggregate, newObjectChanges, changedProperty, toCommitId, snapshotType, loadCommitProps);
    }
}