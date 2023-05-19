package org.summerb.validation.jakarta.test_data3;

import org.summerb.i18n.HasMessageCode;
import org.summerb.validation.ValidationContext;
import org.summerb.validation.jakarta.processors.abstracts.AnnotationProcessorNullableAbstract;

import javax.validation.constraints.Min;

public abstract class AbstractGenericImpl<T> extends AnnotationProcessorNullableAbstract<Min>
    implements HasMessageCode {

  protected abstract T get();

  public AbstractGenericImpl(Min annotation, String propertyName) {
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
