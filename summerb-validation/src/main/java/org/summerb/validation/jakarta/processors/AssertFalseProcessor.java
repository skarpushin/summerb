package org.summerb.validation.jakarta.processors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.jakarta.processors.abstracts.AnnotationProcessorNullableAbstract;

import com.google.common.base.Preconditions;

import jakarta.validation.constraints.AssertFalse;

public class AssertFalseProcessor extends AnnotationProcessorNullableAbstract<AssertFalse> {

  public AssertFalseProcessor(@Nonnull AssertFalse annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  protected void internalValidate(Object value, ValidationContext<?> ctx) {
    Preconditions.checkArgument(value instanceof Boolean, "Argument must be of boolean type");
    ctx.isFalse((Boolean) value, propertyName);
  }
}
