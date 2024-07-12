package org.summerb.easycrud.impl;

import java.util.function.Function;
import org.summerb.easycrud.api.dto.OrderBy;
import org.summerb.methodCapturers.PropertyNameResolver;

public class OrderByBuilder<TRow> {
  protected PropertyNameResolver<TRow> propertyNameResolver;
  protected Function<TRow, ?> getter;
  protected String fieldName;

  public OrderByBuilder(PropertyNameResolver<TRow> propertyNameResolver, Function<TRow, ?> getter) {
    this.propertyNameResolver = propertyNameResolver;
    this.getter = getter;
    this.fieldName = propertyNameResolver.resolve(getter);
  }

  public OrderBy asc() {
    return OrderBy.Asc(fieldName);
  }

  public OrderBy desc() {
    return OrderBy.Desc(fieldName);
  }
}
