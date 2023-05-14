package org.summerb.validation.jakarta.processors;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.errors.MustNotBeEmpty;
import org.summerb.validation.jakarta.processors.abstracts.AnnotationProcessorAbstract;

import com.google.common.base.Preconditions;

import jakarta.validation.constraints.NotEmpty;

public class NotEmptyProcessor extends AnnotationProcessorAbstract<NotEmpty> {

  public NotEmptyProcessor(@Nonnull NotEmpty annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  public void validate(@Nullable Object value, @Nonnull ValidationContext<?> ctx) {
    Preconditions.checkArgument(ctx != null, CTX_REQUIRED);

    if (value == null) {
      ctx.notNull(null, propertyName);
      return;
    }

    if (value instanceof CharSequence) {
      String valueStr =
          value instanceof String ? (String) value : ((CharSequence) value).toString();
      ctx.lengthGreater(valueStr, 0, propertyName);
    } else if (value instanceof Collection<?>) {
      ctx.notEmpty((Collection<?>) value, propertyName);
    } else if (value instanceof Map<?, ?>) {
      if (((Map<?, ?>) value).isEmpty()) {
        ctx.add(new MustNotBeEmpty(propertyName));
      }
    } else if (value instanceof Object[]) {
      if (((Object[]) value).length == 0) {
        ctx.add(new MustNotBeEmpty(propertyName));
      }
    } else {
      throw new IllegalArgumentException(
          "Type " + value.getClass() + " is not supported by NotEmpty annotation");
    }
  }
}
