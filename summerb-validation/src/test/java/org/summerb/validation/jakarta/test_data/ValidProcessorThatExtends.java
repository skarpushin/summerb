package org.summerb.validation.jakarta.test_data;

import org.summerb.i18n.HasMessageCode;
import org.summerb.validation.ValidationContext;
import org.summerb.validation.jakarta.processors.abstracts.AnnotationProcessorNullableAbstract;

import com.google.common.base.Preconditions;

import jakarta.validation.constraints.AssertFalse;

public class ValidProcessorThatExtends extends AnnotationProcessorNullableAbstract<AssertFalse>
    implements HasMessageCode {

  public ValidProcessorThatExtends(AssertFalse annotation, String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  protected void internalValidate(Object value, ValidationContext<?> ctx) {
    Preconditions.checkArgument(value instanceof Boolean, "Argument must be of boolean type");
    ctx.isFalse((Boolean) value, propertyName);
  }

  @Override
  public String getMessageCode() {
    return null;
  }
}
