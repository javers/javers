package org.javers.repository.api;

import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitId;
import org.javers.core.metamodel.object.SnapshotType;
import org.javers.repository.jql.QueryBuilder;

import java.time.Instant;
import java.time.LocalDateTime;

import java.util.*;

/**
 * @author michal wesolowski
 */
public class QueryParamsBuilder {
    private int limit;
    private int skip;
    private LocalDateTime from;
    private Instant fromInstant;
    private LocalDateTime to;
    private Instant toInstant;
    private CommitId toCommitId;
    private Set<CommitId> commitIds = new HashSet<>();
    private Long version;
    private String author;
    private boolean aggregate;
    private Map<String, String> commitProperties = new HashMap<>();
    private Map<String,String> commitPropertiesLike = new HashMap<>();
    private Set<String> changedProperties = new HashSet<>();
    private SnapshotType snapshotType;
    private boolean loadCommitProps = true;
    private Integer snapshotQueryLimit;

    public static QueryParamsBuilder copy(QueryParams that) {
        QueryParamsBuilder copy =  new QueryParamsBuilder(that.limit())
                .skip(that.skip());

        that.from().ifPresent(it -> copy.from(it));
        that.to().ifPresent(it -> copy.to(it));
        that.fromInstant().ifPresent(it -> copy.fromInstant(it));
        that.toInstant().ifPresent(it -> copy.toInstant(it));
        that.toCommitId().ifPresent((it -> copy.toCommitId(it)));
        copy.commitIds = that.commitIds();
        that.version().ifPresent((it -> copy.version(it)));
        that.author().ifPresent((it -> copy.author(it)));
        copy.withChildValueObjects(that.isAggregate());
        copy.commitProperties = that.commitProperties();
        copy.commitPropertiesLike = that.commitPropertiesLike();
        copy.changedProperties = that.changedProperties();
        that.snapshotType().ifPresent((it -> copy.withSnapshotType(it)));
        copy.loadCommitProps = that.isLoadCommitProps();
        that.snapshotQueryLimit().ifPresent((it -> copy.snapshotQueryLimit(it)));

        return copy;
    }

    private QueryParamsBuilder(int limit) {
        this.limit = limit;
        this.skip = 0;
    };

    /**
     *  Initializes builder with a given limit
     *
     *  @see QueryBuilder#limit(int)
     */
    public static QueryParamsBuilder withLimit(int limit) {
        checkLimit(limit);
        return new QueryParamsBuilder(limit);
    }



    /**
     * @see QueryBuilder#snapshotQueryLimit(int)
     */
    public QueryParamsBuilder snapshotQueryLimit(Integer snapshotQueryLimit) {
        this.snapshotQueryLimit = snapshotQueryLimit;
        return this;
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
     * @see QueryBuilder#fromInstant(Instant)
     */
    public QueryParamsBuilder fromInstant(Instant fromInstant) {
        this.fromInstant = fromInstant;
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
     * @see QueryBuilder#toInstant(Instant)
     */
    public QueryParamsBuilder toInstant(Instant toInstant) {
        this.toInstant = toInstant;
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
        if (commitIds != null) {
            this.commitIds.addAll(commitIds);
        }
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
     * @see QueryBuilder#withCommitPropertyLike(String, String)
     */
    public QueryParamsBuilder commitPropertyLike(String name, String value) {
        this.commitPropertiesLike.put(name, value);
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
     * @see QueryBuilder#withSnapshotType(SnapshotType)
     */
    public QueryParamsBuilder withSnapshotType(SnapshotType snapshotType) {
        this.snapshotType = snapshotType;
        return this;
    }

    /**
     * @see QueryBuilder#withChangedPropertyIn(String...)
     */
    public QueryParamsBuilder changedProperties(Collection<String> propertyNames) {
        this.changedProperties.addAll(propertyNames);
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
        return new QueryParams(limit, skip, from, fromInstant, to, toInstant, commitIds, version, author, commitProperties, commitPropertiesLike, aggregate, changedProperties, toCommitId, snapshotType, loadCommitProps, snapshotQueryLimit);
    }
}
