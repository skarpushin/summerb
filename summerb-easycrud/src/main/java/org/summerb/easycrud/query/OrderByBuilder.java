package org.summerb.easycrud.query;

import java.util.function.Function;
import org.summerb.methodCapturers.PropertyNameResolver;

public class OrderByBuilder<TRow> {
  protected PropertyNameResolver<TRow> propertyNameResolver;
  protected Function<TRow, ?> getter;
  protected String fieldName;

  /** Originating query */
  protected Query<?, ?> query;

  public OrderByBuilder(PropertyNameResolver<TRow> propertyNameResolver, Function<TRow, ?> getter) {
    this(propertyNameResolver, getter, null);
  }

  public OrderByBuilder(
      PropertyNameResolver<TRow> propertyNameResolver,
      Function<TRow, ?> getter,
      Query<?, ?> query) {
    this.propertyNameResolver = propertyNameResolver;
    this.getter = getter;
    this.query = query;
    this.fieldName = propertyNameResolver.resolve(getter);
  }

  public OrderBy asc() {
    return new OrderBy(fieldName, OrderBy.ORDER_ASC, query);
  }

  public OrderBy desc() {
    return new OrderBy(fieldName, OrderBy.ORDER_DESC, query);
  }
}
