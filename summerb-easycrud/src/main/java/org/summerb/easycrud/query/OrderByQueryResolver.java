package org.summerb.easycrud.query;

public final class OrderByQueryResolver {
  private OrderByQueryResolver() {}

  public static Query<?, ?> get(OrderBy orderBy) {
    return orderBy.query;
  }
}
