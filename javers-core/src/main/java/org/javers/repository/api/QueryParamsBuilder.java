package org.javers.repository.api;

/**
 * @author michal wesolowski
 */
public class QueryParamsBuilder {
  private int limit;

  private QueryParamsBuilder(int limit) {
    this.limit = limit;
  };

  /**
   * Initializes builder with given limit
   *
   * @param limit choose reasonable limit (number of objects to fetch),
   *              production database could contain more records than you expect
   */
  public static QueryParamsBuilder withLimit(int limit) {
    checkLimit(limit);
    return new QueryParamsBuilder(limit);
  }

  /**
   * Initializes builder with param values from given queryParams instance
   *
   * @param queryParams instance to initialize builder param values from
   */
  public static QueryParamsBuilder initializeWith(QueryParams queryParams) {
    if (queryParams == null) {
      throw new IllegalArgumentException("Query params to copy properties from is null.");
    }
    return new QueryParamsBuilder(queryParams.getLimit());
  }

  /**
   * @param limit choose reasonable limit (number of objects to fetch),
   *              production database could contain more records than you expect
   */
  public QueryParamsBuilder limit(int limit) {
    checkLimit(limit);
    this.limit = limit;
    return this;
  }

  private static void checkLimit(int limit) {
    if (limit <= 0) {
      throw new IllegalArgumentException("Limit is not a positive number.");
    }
  }

  public QueryParams build() {
    return new QueryParams(limit);
  }
}