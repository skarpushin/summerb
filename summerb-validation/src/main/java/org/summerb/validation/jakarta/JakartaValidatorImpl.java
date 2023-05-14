package org.summerb.validation.jakarta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.summerb.validation.ValidationContext;

import com.google.common.base.Preconditions;

public class JakartaValidatorImpl implements JakartaValidator {
  protected final JakartaValidationBeanProcessor jakartaValidationBeanProcessor;

  public JakartaValidatorImpl(
      @Nonnull JakartaValidationBeanProcessor jakartaValidationBeanProcessor) {
    Preconditions.checkArgument(
        jakartaValidationBeanProcessor != null, "jakartaValidationBeanProcessor required");

    this.jakartaValidationBeanProcessor = jakartaValidationBeanProcessor;
  }

  @Override
  public <TBeanClass> void findValidationErrors(
      @Nullable TBeanClass subject, @Nonnull ValidationContext<TBeanClass> validationContext) {
    if (subject == null) {
      return;
    }

    jakartaValidationBeanProcessor
        .getValidationsFor(subject.getClass())
        .forEach(x -> x.process(subject, validationContext));
  }
}
