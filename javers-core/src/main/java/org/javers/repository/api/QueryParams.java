package org.javers.repository.api;

import org.javers.common.string.ToStringBuilder;
import org.javers.core.metamodel.object.SnapshotType;
import org.javers.repository.jql.QueryBuilder;

import java.time.Instant;
import java.util.*;

import org.javers.core.commit.CommitId;

import java.time.LocalDateTime;

/**
 * Container for additional query parameters
 * used for filtering Snapshots to be fetched from database.
 *
 * @author michal wesolowski
 * @see QueryParamsBuilder
 */
public class QueryParams {
    private final int limit;
    private final int skip;
    private final LocalDateTime from;
    private final Instant fromInstant;
    private final LocalDateTime to;
    private final Instant toInstant;
    private final CommitId toCommitId;
    private final Set<CommitId> commitIds;
    private final Long version;
    private final String author;
    private final Map<String, String> commitProperties;
    private final Map<String, String> commitPropertiesLike;
    private final boolean aggregate;
    private final Set<String> changedProperties;
    private final SnapshotType snapshotType;
    private final boolean loadCommitProps;
    private final Integer snapshotQueryLimit;

    QueryParams(int limit, int skip, LocalDateTime from, Instant fromInstant, LocalDateTime to, Instant toInstant, Set<CommitId> commitIds, Long version, String author, Map<String, String> commitProperties, Map<String,String> commitPropertiesLike, boolean aggregate, Set<String> changedProperties, CommitId toCommitId, SnapshotType snapshotType, boolean loadCommitProps, Integer snapshotQueryLimit) {
        this.snapshotQueryLimit = snapshotQueryLimit;
        this.limit = limit;
        this.skip = skip;
        this.from = from;
        this.fromInstant = fromInstant;
        this.to = to;
        this.toInstant = toInstant;
        this.commitIds = Collections.unmodifiableSet(commitIds);
        this.version = version;
        this.author = author;
        this.commitProperties = Collections.unmodifiableMap(commitProperties);
        this.aggregate = aggregate;
        this.changedProperties = Collections.unmodifiableSet(changedProperties);
        this.commitPropertiesLike = Collections.unmodifiableMap(commitPropertiesLike);
        this.toCommitId = toCommitId;
        this.snapshotType = snapshotType;
        this.loadCommitProps = loadCommitProps;
    }

    public QueryParams changeAggregate(boolean newAggregate) {
        return QueryParamsBuilder.copy(this)
                .withChildValueObjects(newAggregate).build();
    }

    public QueryParams nextPage() {
        return QueryParamsBuilder.copy(this)
                .skip(skip + limit).build();
    }

    /**
     * @see QueryBuilder#limit(int)
     */
    public int limit() {
        return limit;
    }

    /**
     * @see QueryBuilder#skip(int)
     */
    public int skip() {
        return skip;
    }

    public boolean hasSnapshotQueryLimit() {
        return snapshotQueryLimit != null;
    }

    public Optional<Integer> snapshotQueryLimit() {
        return Optional.ofNullable(snapshotQueryLimit);
    }

    /**
     * @see QueryBuilder#from(LocalDateTime)
     */
    public Optional<LocalDateTime> from() {
        return Optional.ofNullable(from);
    }

    /**
     * @see QueryBuilder#fromInstant(Instant)
     */
    public Optional<Instant> fromInstant() {
        return Optional.ofNullable(fromInstant);
    }

    /**
     * @see QueryBuilder#to(LocalDateTime)
     */
    public Optional<LocalDateTime> to() {
        return Optional.ofNullable(to);
    }

    /**
     * @see QueryBuilder#toInstant(Instant)
     */
    public Optional<Instant> toInstant() {
        return Optional.ofNullable(toInstant);
    }

    /**
     * @see QueryBuilder#toCommitId(CommitId)
     */
    public Optional<CommitId> toCommitId() {
        return Optional.ofNullable(toCommitId);
    }

    /**
     * @see QueryBuilder#withCommitIds(Collection)
     */
    public Set<CommitId> commitIds() {
        return Collections.unmodifiableSet(commitIds);
    }

    /**
     * @see QueryBuilder#withCommitProperty(String, String)
     */
    public Map<String, String> commitProperties() {
        return commitProperties != null ?
                commitProperties : Collections.emptyMap();
    }

    /**
     * @see QueryBuilder#withCommitPropertyLike/(String, String)
     */
    public Map<String, String> commitPropertiesLike() {
        return commitPropertiesLike != null ?
            commitPropertiesLike : Collections.emptyMap();
    }

    /**
     * @see QueryBuilder#withChangedPropertyIn(String...)
     */
    public Set<String> changedProperties() {
        return Collections.unmodifiableSet(changedProperties);
    }

    /**
     * @see QueryBuilder#withVersion(long)
     */
    public Optional<Long> version() {
        return Optional.ofNullable(version);
    }

    /**
     * @see QueryBuilder#byAuthor(String)
     */
    public Optional<String> author() {
        return Optional.ofNullable(author);
    }

    /**
     * @see QueryBuilder#withChildValueObjects()
     */
    public boolean isAggregate() {
        return aggregate;
    }

    public boolean isLoadCommitProps() {
        return loadCommitProps;
    }

    /**
     * @see QueryBuilder#withSnapshotType(SnapshotType)
     */
    public Optional<SnapshotType> snapshotType() {
        return Optional.ofNullable(snapshotType);
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this,
                "aggregate", aggregate,
                "from", from,
                "fromInstant", fromInstant,
                "to", to,
                "toInstant", toInstant,
                "toCommitId", toCommitId,
                "commitIds", commitIds,
                "changeProperties", changedProperties,
                "version", version,
                "author", author,
                "snapshotType", snapshotType,
                "limit", limit,
                "skip", skip,
                "snapshotQueryLimit", snapshotQueryLimit);
    }
}
