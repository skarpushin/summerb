package org.summerb.validation.jakarta.test_data2;

import org.summerb.validation.ValidationContext;

import jakarta.validation.constraints.Max;

public class ValidViaAbstractInterfaceGeneric implements AbstractInterfaceGeneric<Boolean> {
  private String propertyName;

  public ValidViaAbstractInterfaceGeneric(Max annotation, String propertyName) {
    this.propertyName = propertyName;
  }

  @Override
  public void validate(Object value, ValidationContext<?> ctx) {
    ctx.notNull(null, propertyName);
  }

  @Override
  public void method(Boolean t) {}
}
