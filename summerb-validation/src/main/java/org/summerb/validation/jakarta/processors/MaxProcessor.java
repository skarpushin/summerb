package org.summerb.validation.jakarta.processors;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.jakarta.processors.abstracts.DecimalProcessorNullableAbstract;

import jakarta.validation.constraints.Max;

public class MaxProcessor extends DecimalProcessorNullableAbstract<Max> {

  public MaxProcessor(@Nonnull Max annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  protected void internalValidate(BigDecimal value, ValidationContext<?> ctx) {
    ctx.le(value.longValue(), annotation.value(), propertyName);
  }
}
