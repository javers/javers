package org.javers.repository.api;

import org.javers.common.collections.Optional;
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
    private final Optional<LocalDateTime> from;
    private final Optional<LocalDateTime> to;

    QueryParams(int limit, LocalDateTime from, LocalDateTime to) {
        this.limit = limit;
        this.from = Optional.fromNullable(from);
        this.to = Optional.fromNullable(to);
    }

    public int limit() {
        return limit;
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

    @Override
    public String toString() {
        return "QueryParams{" +
                "limit=" + limit +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
