package org.summerb.validation.jakarta.processors.abstracts;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationContext;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public abstract class NumberProcessorNullableAbstract<T extends Annotation>
    extends AnnotationProcessorNullableAbstract<T> {

  public NumberProcessorNullableAbstract(@Nonnull T annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  protected void internalValidate(Object value, ValidationContext<?> ctx) {
    Preconditions.checkArgument(value instanceof Number, "value must be of type Number");
    internalValidate(getSignum((Number) value), ctx);
  }

  @VisibleForTesting
  public static int getSignum(Number value) {
    if (value instanceof BigDecimal) {
      return ((BigDecimal) value).signum();
    } else if (value instanceof BigInteger) {
      return ((BigInteger) value).signum();
    } else if (value instanceof Byte
        || value instanceof Integer
        || value instanceof Long
        || value instanceof Short) {
      long val = value.longValue();
      return val < 0 ? -1 : (val > 0 ? 1 : 0);
    } else if (value instanceof Double || value instanceof Float) {
      double val = value.doubleValue();
      return val < 0 ? -1 : (val > 0 ? 1 : 0);
    } else {
      throw new IllegalArgumentException("Type is not supported: " + value.getClass());
    }
  }

  protected abstract void internalValidate(int signum, ValidationContext<?> ctx);
}
