package org.summerb.validation;

import javax.annotation.Nonnull;

public interface ValidationContextFactory {

  /**
   * @param <T> type of Bean
   * @param bean Bean which getters will be used to extract field names and values
   * @return instance that can be used for both - referring to fields using method references as
   *     well as string literals
   */
  @Nonnull
  <T, F extends ValidationContext<T>> F buildFor(@Nonnull T bean);

  /**
   * @return instance that can be used only to refer to fields using string literals. Not
   *     recommended as in such case you'll use string literals and loose all power of IDE and
   *     Compiler static code analysis
   */
  @Nonnull
  ValidationContext<?> build();
}
