package org.summerb.validation.jakarta.processors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.jakarta.processors.abstracts.AnnotationProcessorAbstract;

import com.google.common.base.Preconditions;

import jakarta.validation.constraints.NotBlank;

public class NotBlankProcessor extends AnnotationProcessorAbstract<NotBlank> {

  public NotBlankProcessor(@Nonnull NotBlank annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  public void validate(@Nullable Object value, @Nonnull ValidationContext<?> ctx) {
    Preconditions.checkArgument(ctx != null, CTX_REQUIRED);

    if (!ctx.notNull(value, propertyName)) {
      return;
    }

    Preconditions.checkArgument(
        value instanceof CharSequence, "Argument must be of CharSequence type");
    String valueStr = value instanceof String ? (String) value : ((CharSequence) value).toString();
    ctx.hasText(valueStr, propertyName);
  }
}
