package org.summerb.validation.jakarta.test_data;

import org.summerb.i18n.HasMessageCode;
import org.summerb.validation.ValidationContext;
import org.summerb.validation.jakarta.processors.abstracts.AnnotationProcessorNullableAbstract;

import jakarta.validation.constraints.NotNull;

public abstract class AbstractImpl extends AnnotationProcessorNullableAbstract<NotNull>
    implements HasMessageCode {

  public AbstractImpl(NotNull annotation, String propertyName) {
    super(annotation, propertyName);
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
