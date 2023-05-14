package org.summerb.validation.jakarta.processors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.errors.MustBeGreater;
import org.summerb.validation.jakarta.processors.abstracts.NumberProcessorNullableAbstract;

import jakarta.validation.constraints.Positive;

public class PositiveProcessor extends NumberProcessorNullableAbstract<Positive> {
  public PositiveProcessor(@Nonnull Positive annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  protected void internalValidate(int signum, ValidationContext<?> ctx) {
    if (signum > 0) {
      return;
    }
    ctx.add(new MustBeGreater(propertyName, 0));
  }
}
