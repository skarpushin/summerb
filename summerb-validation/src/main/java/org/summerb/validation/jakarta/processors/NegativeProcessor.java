package org.summerb.validation.jakarta.processors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.errors.MustBeLess;
import org.summerb.validation.jakarta.processors.abstracts.NumberProcessorNullableAbstract;

import jakarta.validation.constraints.Negative;

public class NegativeProcessor extends NumberProcessorNullableAbstract<Negative> {

  public NegativeProcessor(@Nonnull Negative annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  protected void internalValidate(int signum, ValidationContext<?> ctx) {
    if (signum < 0) {
      return;
    }
    ctx.add(new MustBeLess(propertyName, 0));
  }
}
