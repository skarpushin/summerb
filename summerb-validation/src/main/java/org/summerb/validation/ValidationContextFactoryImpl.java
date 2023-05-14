package org.summerb.validation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.summerb.methodCapturers.PropertyNameObtainer;
import org.summerb.methodCapturers.PropertyNameObtainerFactory;
import org.summerb.validation.jakarta.JakartaValidator;

import com.google.common.base.Preconditions;

public class ValidationContextFactoryImpl implements ValidationContextFactory {

  protected final JakartaValidator jakartaValidator;
  protected final PropertyNameObtainerFactory propertyNameObtainerFactory;

  public ValidationContextFactoryImpl(
      @Nonnull PropertyNameObtainerFactory propertyNameObtainerFactory,
      @Nullable JakartaValidator jakartaValidator) {
    Preconditions.checkArgument(propertyNameObtainerFactory != null);
    this.propertyNameObtainerFactory = propertyNameObtainerFactory;
    this.jakartaValidator = jakartaValidator;
  }

  @SuppressWarnings("unchecked")
  @Override
  public @Nonnull <T, F extends ValidationContext<T>> F buildFor(@Nonnull T bean) {
    Preconditions.checkArgument(bean != null);
    try {
      PropertyNameObtainer<T> obtainer =
          propertyNameObtainerFactory.getObtainer((Class<T>) bean.getClass());
      return (F) new ValidationContext<T>(bean, obtainer, jakartaValidator, this);
    } catch (Exception e) {
      throw new RuntimeException("Failed to build ValidationContext for " + bean, e);
    }
  }

  @Override
  public @Nonnull ValidationContext<?> build() {
    return new ValidationContext<>();
  }
}
