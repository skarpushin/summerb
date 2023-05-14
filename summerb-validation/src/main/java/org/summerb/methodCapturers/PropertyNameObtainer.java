package org.summerb.methodCapturers;

import java.util.function.Function;

import javax.annotation.Nonnull;

/**
 * Impl of this interface will be capable of the obtaining method name from method reference for
 * some <b>particular</b> POJO. If you need to obtain field names from different POJO classes, then
 * you need to obtain separate instances of {@link PropertyNameObtainer}
 *
 * <p>Specific instances are supposed to be obtained via {@link PropertyNameObtainerFactory}
 *
 * @author Sergey Karpushin
 * @param <T> type of the bean
 */
public interface PropertyNameObtainer<T> {

  /**
   * @param methodReference method reference, i.e. SomePojo::getName
   * @return name of the method which is used for method reference
   */
  @Nonnull
  String obtainFrom(@Nonnull Function<T, ?> methodReference);
}
