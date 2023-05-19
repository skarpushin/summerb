package org.summerb.validation.jakarta.test_data;

import javax.validation.constraints.NotNull;

import org.summerb.i18n.HasMessageCode;
import org.summerb.validation.ValidationContext;

public class ValidViaAbstractImpl extends AbstractImpl implements HasMessageCode {

  public ValidViaAbstractImpl(NotNull annotation, String propertyName) {
    super(annotation, propertyName);
    throw new IllegalStateException("test");
  }

  @Override
  protected void internalValidate(Object value, ValidationContext<?> ctx) {
    ctx.notNull(value, propertyName);
  }

  @Override
  public String getMessageCode() {
    return null;
  }
}
