package org.summerb.validation.jakarta.test_data;

import org.summerb.validation.ValidationContext;

import javax.validation.constraints.Pattern;

public class ValidViaAbstractInterface implements AbstractInterface {
  private String propertyName;

  public ValidViaAbstractInterface(Pattern annotation, String propertyName) {
    this.propertyName = propertyName;
  }

  @Override
  public void validate(Object value, ValidationContext<?> ctx) {
    ctx.notNull(null, propertyName);
  }
}
