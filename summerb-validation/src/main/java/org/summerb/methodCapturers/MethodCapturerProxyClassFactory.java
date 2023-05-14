package org.summerb.methodCapturers;

import java.lang.reflect.Method;

import javax.annotation.Nonnull;

/**
 * Impl of this interface supposed to be able to build Proxies which is both: extends provided POJO
 * class and {@link MethodCapturer} interface
 *
 * @author Sergey Karpushin
 */
public interface MethodCapturerProxyClassFactory {

  /**
   * @param clazz POJO/Bean class for which we want to be able to capture names of invoked methods
   * @return new instance of MethodCapturer for given clazz -use it to obtain names (actually whole
   *     {@link Method} instances) of invoked methods
   */
  @Nonnull
  MethodCapturer buildProxyFor(@Nonnull Class<?> clazz);
}
