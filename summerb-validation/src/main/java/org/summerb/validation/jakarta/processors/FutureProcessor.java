package org.summerb.validation.jakarta.processors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.jakarta.processors.abstracts.AnnotationProcessorNullableAbstract;

import com.google.common.base.Preconditions;

import jakarta.validation.constraints.Future;

public class FutureProcessor extends AnnotationProcessorNullableAbstract<Future> {

  public FutureProcessor(@Nonnull Future annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  protected void internalValidate(Object value, ValidationContext<?> ctx) {
    Preconditions.checkArgument(
        value instanceof Comparable, "value must be at least of Comparable type");
    ctx.future((Comparable) value, propertyName);
  }
}
