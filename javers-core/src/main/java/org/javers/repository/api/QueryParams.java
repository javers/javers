package org.javers.repository.api;

import org.javers.common.collections.Optional;
import org.javers.core.commit.CommitId;
import org.joda.time.LocalDateTime;

/**
 * Container for additional query parameters
 * used for filtering Snapshots to be fetched from database.
 * <br/>
 *
 * Query parameters can't affect query semantic
 * as they are used by all types of queries.
 *
 * @see QueryParamsBuilder
 *
 * @author michal wesolowski
 */
public class QueryParams {
    private final int limit;
    private final int skip;
    private final Optional<LocalDateTime> from;
    private final Optional<LocalDateTime> to;
    private final Optional<CommitId> commitId;
    private final Optional<Long> version;
    private final Optional<String> author;

    QueryParams(int limit, int skip, LocalDateTime from, LocalDateTime to, CommitId commitId, Long version, String author) {
        this.limit = limit;
        this.skip = skip;
        this.from = Optional.fromNullable(from);
        this.to = Optional.fromNullable(to);
        this.commitId = Optional.fromNullable(commitId);
        this.version = Optional.fromNullable(version);
        this.author = Optional.fromNullable(author);
    }

    public int limit() {
        return limit;
    }

    public int skip() {
        return skip;
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

    /**
     * filters results to Snapshots created after given date
     */
    public Optional<LocalDateTime> from() {
        return from;
    }

    /**
     * filters results to Snapshots created before given date
     */
    public Optional<LocalDateTime> to() {
        return to;
    }

    /*
     * filters results to Snapshots with a given commitId
     */
    public Optional<CommitId> commitId() {
        return commitId;
    }

    /*
     * filters results to Snapshots with a given version
     */
    public Optional<Long> version() {
        return version;
    }

    /*
     * filters results to Snapshots committed by a given author
     */
    public Optional<String> author() {
        return author;
    }

    @Override
    public String toString() {
        return "QueryParams{" +
                "limit=" + limit +
                ", skip=" + skip +
                ", from=" + from +
                ", to=" + to +
                ", commitId=" + commitId +
                ", version=" + version +
                "}";
    }
}
