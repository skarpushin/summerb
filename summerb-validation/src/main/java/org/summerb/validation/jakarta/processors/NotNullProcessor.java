package org.summerb.validation.jakarta.processors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.jakarta.processors.abstracts.AnnotationProcessorAbstract;

import com.google.common.base.Preconditions;

import jakarta.validation.constraints.NotNull;

public class NotNullProcessor extends AnnotationProcessorAbstract<NotNull> {

  public NotNullProcessor(@Nonnull NotNull annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  public void validate(Object value, ValidationContext<?> ctx) {
    Preconditions.checkArgument(ctx != null, CTX_REQUIRED);
    ctx.notNull(value, propertyName);
  }
}
