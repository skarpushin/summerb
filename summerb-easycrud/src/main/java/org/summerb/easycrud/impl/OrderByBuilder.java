package org.summerb.easycrud.impl;

import java.util.function.Function;
import org.summerb.easycrud.api.dto.OrderBy;
import org.summerb.methodCapturers.PropertyNameObtainer;

public class OrderByBuilder<TRow> {
  protected PropertyNameObtainer<TRow> propertyNameObtainer;
  protected Function<TRow, ?> getter;
  protected String fieldName;

  public OrderByBuilder(PropertyNameObtainer<TRow> propertyNameObtainer, Function<TRow, ?> getter) {
    this.propertyNameObtainer = propertyNameObtainer;
    this.getter = getter;
    this.fieldName = propertyNameObtainer.obtainFrom(getter);
  }

  public OrderBy asc() {
    return OrderBy.Asc(fieldName);
  }

  public OrderBy desc() {
    return OrderBy.Desc(fieldName);
  }
}
