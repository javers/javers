package org.javers.repository.api;

import org.joda.time.LocalDateTime;

/**
 * Container for parameters used during query execution
 * Use QueryParamsBuilder to build instances of this class
 *
 * @author michal wesolowski
 */
public class QueryParams {
  private final int limit;
  private final LocalDateTime from;
  private final LocalDateTime to;

  QueryParams(int limit, LocalDateTime from, LocalDateTime to) {
    this.limit = limit;
    this.from = from;
    this.to = to;
  }

  public int getLimit() {
    return limit;
  }

  public boolean isFromSet() {
    return from != null;
  }

  public LocalDateTime getFrom() {
    if (!isFromSet()) {
      throw new IllegalStateException("Param 'from' is not set.");
    }
    return from;
  }

  public boolean isToSet() {
    return to != null;
  }

  public LocalDateTime getTo() {
    if (!isToSet()) {
      throw new IllegalStateException("Param 'to' is not set.");
    }
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
