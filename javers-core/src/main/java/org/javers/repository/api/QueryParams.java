package org.javers.repository.api;

import org.javers.common.string.ToStringBuilder;
import org.javers.core.metamodel.object.SnapshotType;
import org.javers.repository.jql.QueryBuilder;

import java.util.*;

import org.javers.core.commit.CommitId;
import java.time.LocalDateTime;

/**
 * Container for additional query parameters
 * used for filtering Snapshots to be fetched from database.
 *
 * @see QueryParamsBuilder
 * @author michal wesolowski
 */
public class QueryParams {
    private final int limit;
    private final int skip;
    private final Optional<LocalDateTime> from;
    private final Optional<LocalDateTime> to;
    private final Optional<CommitId> toCommitId;
    private final Set<CommitId> commitIds;
    private final Optional<Long> version;
    private final Optional<String> author;
    private final Optional<Map<String, String>> commitProperties;
    private final boolean aggregate;
    private final boolean newObjectChanges;
    private final Optional<String> changedProperty;
    private final Optional<SnapshotType> snapshotType;

    QueryParams(int limit, int skip, LocalDateTime from, LocalDateTime to, Set<CommitId> commitIds, Long version, String author, Map<String, String> commitProperties, boolean aggregate, boolean newObjectChanges, String changedProperty, CommitId toCommitId, SnapshotType snapshotType) {
        this.limit = limit;
        this.skip = skip;
        this.from = Optional.ofNullable(from);
        this.to = Optional.ofNullable(to);
        this.commitIds = commitIds;
        this.version = Optional.ofNullable(version);
        this.author = Optional.ofNullable(author);
        this.commitProperties = Optional.ofNullable(commitProperties);
        this.aggregate = aggregate;
        this.newObjectChanges = newObjectChanges;
        this.changedProperty = Optional.ofNullable(changedProperty);
        this.toCommitId = Optional.ofNullable(toCommitId);
        this.snapshotType = Optional.ofNullable(snapshotType);
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

    /**
     * @see QueryBuilder#from(LocalDateTime)
     */
    public Optional<LocalDateTime> from() {
        return from;
    }

    /**
     * @see QueryBuilder#to(LocalDateTime)
     */
    public Optional<LocalDateTime> to() {
        return to;
    }

    /**
     * @see QueryBuilder#toCommitId(CommitId)
     */
    public Optional<CommitId> toCommitId() {
        return toCommitId;
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
        return commitProperties.isPresent() ?
            commitProperties.get() : Collections.<String, String>emptyMap();
    }

    /**
     * @see QueryBuilder#withChangedProperty(String)
     */
    public Optional<String> changedProperty(){
        return changedProperty;
    }

    /**
     * @see QueryBuilder#withVersion(long)
     */
    public Optional<Long> version() {
        return version;
    }

    /**
     * @see QueryBuilder#byAuthor(String)
     */
    public Optional<String> author() {
        return author;
    }

    /**
     * @see QueryBuilder#withChildValueObjects()
     */
    public boolean isAggregate() {
        return aggregate;
    }

    /**
     * @see QueryBuilder#withSnapshotType(SnapshotType)
     */
    public Optional<SnapshotType> snapshotType() {
        return snapshotType;
    }

    /**
     * @see QueryBuilder#withNewObjectChanges(boolean)
     */
    public boolean newObjectChanges() {
        return newObjectChanges;
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this,
                "aggregate", aggregate,
                "from", from,
                "to", to,
                "toCommitId", toCommitId,
                "commitIds", commitIds,
                "changedProperty", changedProperty,
                "version", version,
                "author", author,
                "newObjectChanges", newObjectChanges,
                "snapshotType", snapshotType,
                "limit", limit,
                "skip", skip);
    }

    public boolean hasDates() {
        return from.isPresent() || to.isPresent();
    }

    public boolean isDateInRange(LocalDateTime date) {
        if (from.isPresent() && from.get().isAfter(date)){
            return false;
        }
        if (to.isPresent() && to.get().isBefore(date)){
            return false;
        }

        return true;
    }
}
