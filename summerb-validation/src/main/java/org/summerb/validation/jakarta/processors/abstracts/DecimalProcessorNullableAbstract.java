package org.summerb.validation.jakarta.processors.abstracts;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationContext;

import com.google.common.base.Preconditions;

public abstract class DecimalProcessorNullableAbstract<T extends Annotation>
    extends AnnotationProcessorNullableAbstract<T> {

  public static final Set<Class<?>> ALLOWED_TYPES;

  static {
    Set<Class<?>> allowed = new HashSet<>();
    allowed.add(BigDecimal.class);
    allowed.add(BigInteger.class);
    allowed.add(CharSequence.class);
    allowed.add(Byte.class);
    allowed.add(Short.class);
    allowed.add(Integer.class);
    allowed.add(Long.class);
    ALLOWED_TYPES = allowed;
  }

  public DecimalProcessorNullableAbstract(@Nonnull T annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  protected void internalValidate(Object value, ValidationContext<?> ctx) {
    Class<? extends Object> valueType = value.getClass();
    Preconditions.checkArgument(
        ALLOWED_TYPES.contains(valueType) || value instanceof CharSequence,
        "Type is not one of allowed %s: %s",
        ALLOWED_TYPES,
        valueType);

    internalValidate(new BigDecimal(String.valueOf(value)), ctx);
  }

  protected abstract void internalValidate(BigDecimal valueToValidate, ValidationContext<?> ctx);
}
