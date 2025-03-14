package org.summerb.validation.jakarta.test_data2;

import jakarta.validation.constraints.Max;
import org.summerb.validation.ValidationContext;

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
