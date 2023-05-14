package org.summerb.validation.jakarta.processors;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.errors.LengthMustBeBetween;
import org.summerb.validation.jakarta.processors.abstracts.AnnotationProcessorNullableAbstract;

import jakarta.validation.constraints.Size;

public class SizeProcessor extends AnnotationProcessorNullableAbstract<Size> {

  public SizeProcessor(@Nonnull Size annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  protected void internalValidate(Object value, ValidationContext<?> ctx) {
    int len;
    if (value instanceof CharSequence) {
      len = ((CharSequence) value).length();
    } else if (value instanceof Collection<?>) {
      len = ((Collection<?>) value).size();
    } else if (value instanceof Map<?, ?>) {
      len = ((Map<?, ?>) value).size();
    } else if (value instanceof Object[]) {
      len = ((Object[]) value).length;
    } else {
      throw new IllegalArgumentException(
          "Type " + value.getClass() + " is not supported by Size annotation");
    }

    if (annotation.min() <= len && len <= annotation.max()) {
      return;
    }
    ctx.add(new LengthMustBeBetween(propertyName, annotation.min(), annotation.max()));
  }
}
