package org.summerb.validation.jakarta.processors;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.jakarta.processors.abstracts.DecimalProcessorNullableAbstract;

import jakarta.validation.constraints.DecimalMin;

public class DecimalMinProcessor extends DecimalProcessorNullableAbstract<DecimalMin> {

  private BigDecimal boundary;

  public DecimalMinProcessor(@Nonnull DecimalMin annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
    boundary = new BigDecimal(annotation.value());
  }

  @Override
  protected void internalValidate(BigDecimal valueToValidate, ValidationContext<?> ctx) {
    if (annotation.inclusive()) {
      ctx.ge(valueToValidate, boundary, propertyName);
    } else {
      ctx.greater(valueToValidate, boundary, propertyName);
    }
  }
}
