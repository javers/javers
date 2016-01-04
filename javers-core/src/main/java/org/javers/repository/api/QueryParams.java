package org.javers.repository.api;

import org.javers.common.collections.Optional;
import org.joda.time.LocalDateTime;

/**
 * Container for parameters used during query execution
 * Use QueryParamsBuilder to build instances of this class
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

    public Optional<LocalDateTime> from() {
        return from;
    }

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
