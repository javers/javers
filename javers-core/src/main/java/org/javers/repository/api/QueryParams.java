package org.javers.repository.api;

/**
 * Container for parameters used during query execution
 * Use QueryParamsBuilder to build instances of this class
 *
 * @author michal wesolowski
 */
public class QueryParams {
  private int limit;

  QueryParams(int limit) {
    this.limit = limit;
  }

  public int getLimit() {
    return limit;
  }

  @Override
  public String toString() {
    return "QueryParams{" +
        "limit=" + limit +
        '}';
  }
}
