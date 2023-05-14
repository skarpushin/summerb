package org.summerb.validation.jakarta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.summerb.validation.ValidationContext;

public interface JakartaValidator {

  <TBeanClass> void findValidationErrors(
      @Nullable TBeanClass subject, @Nonnull ValidationContext<TBeanClass> validationContext);
}
