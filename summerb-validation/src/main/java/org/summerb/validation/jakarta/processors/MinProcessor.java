package org.summerb.validation.jakarta.processors;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.jakarta.processors.abstracts.DecimalProcessorNullableAbstract;

import jakarta.validation.constraints.Min;

public class MinProcessor extends DecimalProcessorNullableAbstract<Min> {

  public MinProcessor(@Nonnull Min annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  protected void internalValidate(BigDecimal value, ValidationContext<?> ctx) {
    ctx.ge(value.longValue(), annotation.value(), propertyName);
  }
}
