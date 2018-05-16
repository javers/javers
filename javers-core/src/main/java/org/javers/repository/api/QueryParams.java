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
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final CommitId toCommitId;
    private final Set<CommitId> commitIds;
    private final Long version;
    private final String author;
    private final Map<String, String> commitProperties;
    private final AggregateType aggregate;
    private final boolean newObjectChanges;
    private final String changedProperty;
    private final SnapshotType snapshotType;

    QueryParams(int limit, int skip, LocalDateTime from, LocalDateTime to, Set<CommitId> commitIds, Long version, String author, Map<String, String> commitProperties, AggregateType aggregate, boolean newObjectChanges, String changedProperty, CommitId toCommitId, SnapshotType snapshotType) {
        this.limit = limit;
        this.skip = skip;
        this.from = from;
        this.to = to;
        this.commitIds = commitIds;
        this.version = version;
        this.author = author;
        this.commitProperties = commitProperties;
        this.aggregate = aggregate;
        this.newObjectChanges = newObjectChanges;
        this.changedProperty = changedProperty;
        this.toCommitId = toCommitId;
        this.snapshotType = snapshotType;
    }

    @Deprecated
    public static QueryParams forShadowQuery(QueryParams q) {
        return null;
    }

    public static QueryParams forShadowQueryRoots(QueryParams q) {
        return new QueryParams(
            q.limit, q.skip, q.from, q.to, q.commitIds, q.version, q.author, q.commitProperties,
            AggregateType.NONE, q.newObjectChanges, q.changedProperty, q.toCommitId, q.snapshotType);
    }

    public static QueryParams forShadowQueryRootsChildren(QueryParams q) {
        return new QueryParams(
            q.limit * 2, q.skip, q.from, q.to, q.commitIds, q.version, q.author, q.commitProperties,
            AggregateType.CHILD_VALUE_OBJECTS_ONLY, q.newObjectChanges, q.changedProperty, q.toCommitId, q.snapshotType);
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
        return Optional.ofNullable(from);
    }

    public AggregateType aggregateType() {
        return aggregate;
    }

    /**
     * @see QueryBuilder#to(LocalDateTime)
     */
    public Optional<LocalDateTime> to() {
        return Optional.ofNullable(to);
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
     * @see QueryBuilder#withChangedProperty(String)
     */
    public Optional<String> changedProperty(){
        return Optional.ofNullable(changedProperty);
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
     * use {@link #shouldLoadChildValueObjects()} and {@link #shouldLoadEntities()}
     */
    @Deprecated
    public boolean isAggregate() {
        return aggregate == AggregateType.ENTITIES_WITH_CHILD_VALUE_OBJECTS;
    }

    public boolean shouldLoadChildValueObjects() {
        return aggregate == AggregateType.CHILD_VALUE_OBJECTS_ONLY ||
               aggregate == AggregateType.ENTITIES_WITH_CHILD_VALUE_OBJECTS;
    }

    public boolean shouldLoadEntities() {
        return aggregate == AggregateType.NONE ||
               aggregate == AggregateType.ENTITIES_WITH_CHILD_VALUE_OBJECTS;
    }

    /**
     * @see QueryBuilder#withSnapshotType(SnapshotType)
     */
    public Optional<SnapshotType> snapshotType() {
        return Optional.ofNullable(snapshotType);
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
        return from().isPresent() || to().isPresent();
    }

    public boolean isDateInRange(LocalDateTime date) {
        if (from().isPresent() && from().get().isAfter(date)){
            return false;
        }
        if (to().isPresent() && to().get().isBefore(date)){
            return false;
        }

        return true;
    }

    public enum AggregateType {
        NONE,
        ENTITIES_WITH_CHILD_VALUE_OBJECTS,
        CHILD_VALUE_OBJECTS_ONLY,
        /**
         * internal, for core snapshots query
         */
        SHADOW_STYLE
    }
}
