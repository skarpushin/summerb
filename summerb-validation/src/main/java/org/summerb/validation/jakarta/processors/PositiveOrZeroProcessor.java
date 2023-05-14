package org.summerb.validation.jakarta.processors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.errors.MustBeGreaterOrEqual;
import org.summerb.validation.jakarta.processors.abstracts.NumberProcessorNullableAbstract;

import jakarta.validation.constraints.PositiveOrZero;

public class PositiveOrZeroProcessor extends NumberProcessorNullableAbstract<PositiveOrZero> {
  public PositiveOrZeroProcessor(@Nonnull PositiveOrZero annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  protected void internalValidate(int signum, ValidationContext<?> ctx) {
    if (signum >= 0) {
      return;
    }
    ctx.add(new MustBeGreaterOrEqual(propertyName, 0));
  }
}
