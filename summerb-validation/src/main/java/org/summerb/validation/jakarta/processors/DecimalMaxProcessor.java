package org.summerb.validation.jakarta.processors;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.jakarta.processors.abstracts.DecimalProcessorNullableAbstract;

import jakarta.validation.constraints.DecimalMax;

public class DecimalMaxProcessor extends DecimalProcessorNullableAbstract<DecimalMax> {

  private BigDecimal boundary;

  public DecimalMaxProcessor(@Nonnull DecimalMax annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
    boundary = new BigDecimal(annotation.value());
  }

  @Override
  protected void internalValidate(BigDecimal value, ValidationContext<?> ctx) {
    if (annotation.inclusive()) {
      ctx.le(value, boundary, propertyName);
    } else {
      ctx.less(value, boundary, propertyName);
    }
  }
}
