package org.summerb.validation.jakarta;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Impl of this interface supposed to process class to identify all validations that are declared on
 * fields and/or getters/setters.
 *
 * <p>It is advised to wrap actual impl (supposedly {@link JakartaValidationBeanProcessorImpl}) with
 * cached impl, i.e. {@link JakartaValidationBeanProcessorCachedImpl}
 *
 * @author Sergey Karpushin
 */
public interface JakartaValidationBeanProcessor {

  /**
   * @param clazz POJO/Bean class that potentially has Jakarta bean validations applied.
   * @return list of validations found in clazz. Might be empty, never null. Validation annotations
   *     might be found on getters/setters or fields
   */
  @Nonnull
  List<JakartaValidatorItem> getValidationsFor(@Nonnull Class<?> clazz);
}
