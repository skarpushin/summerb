package org.summerb.validation.jakarta.processors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.errors.MustBeLessOrEqual;
import org.summerb.validation.jakarta.processors.abstracts.NumberProcessorNullableAbstract;

import jakarta.validation.constraints.NegativeOrZero;

public class NegativeOrZeroProcessor extends NumberProcessorNullableAbstract<NegativeOrZero> {
  public NegativeOrZeroProcessor(@Nonnull NegativeOrZero annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  protected void internalValidate(int signum, ValidationContext<?> ctx) {
    if (signum <= 0) {
      return;
    }
    ctx.add(new MustBeLessOrEqual(propertyName, 0));
  }
}
