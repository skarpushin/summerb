package org.summerb.validation.jakarta.processors.abstracts;

import java.lang.annotation.Annotation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.summerb.validation.ValidationContext;

import com.google.common.base.Preconditions;

/**
 * Abstract class for processors of constraints annotations which treat null value is valid
 *
 * @author Sergey Karpushin
 * @param <T> type of Annotation
 */
public abstract class AnnotationProcessorNullableAbstract<T extends Annotation>
    extends AnnotationProcessorAbstract<T> {

  public AnnotationProcessorNullableAbstract(@Nonnull T annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  public void validate(@Nullable Object value, @Nonnull ValidationContext<?> ctx) {
    Preconditions.checkArgument(ctx != null, CTX_REQUIRED);

    if (value == null) {
      // NOTE: As per jakarta validation, null values are considered valid (for some of the
      // constraints)
      return;
    }

    internalValidate(value, ctx);
  }

  protected abstract void internalValidate(Object value, ValidationContext<?> ctx);
}
