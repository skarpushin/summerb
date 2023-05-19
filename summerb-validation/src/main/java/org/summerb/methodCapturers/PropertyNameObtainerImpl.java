/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.methodCapturers;

import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;

import net.bytebuddy.ByteBuddy;

/**
 * Reference impl of {@link PropertyNameObtainer} is based on {@link ByteBuddy} that creates proxy
 * and intercepts methods calls using {@link MethodCapturer} "mix-in".
 *
 * @param <T> type of POJO for which the instance of this class will be able to determine names of
 *     properties invoked via getters
 */
public class PropertyNameObtainerImpl<T> implements PropertyNameObtainer<T> {
  // private Logger log = LoggerFactory.getLogger(getClass());

  protected final Supplier<MethodCapturer> methodCapturerSupplier;

  private MethodCapturer methodCapturer;

  /**
   * @param methodCapturerSupplier this is an indirection for obtaining MethodCapturer. it is needed
   *     to enable caching logic as instantiation of MethodCapturer is somewhat expensive operation
   *     and in some cases we don't even need it
   */
  public PropertyNameObtainerImpl(Supplier<MethodCapturer> methodCapturerSupplier) {
    Preconditions.checkArgument(methodCapturerSupplier != null);
    this.methodCapturerSupplier = methodCapturerSupplier;
  }

  @Override
  @SuppressWarnings("unchecked")
  public String obtainFrom(Function<T, ?> methodReference) {
    Preconditions.checkArgument(methodReference != null, "methodReference required");

    // NOTE: Caching is not possible because even for the same field we get different instance of
    // lambda

    getMethodCapturer().set__Method(null);
    try {
      methodReference.apply((T) methodCapturer);
    } catch (Exception e) {
      // NOTE: We're ignoring this exception because user might've provided not only a method
      // reference, but also an arbitrary Lambda function that might've attempted to process results
      // of method reference. We do not care about such issue. If we captured method name -- that is
      // all we need
    }
    Method method = methodCapturer.get__Method();
    Preconditions.checkState(method != null, "Method was not captured");

    Preconditions.checkArgument(
        method.getParameterTypes().length == 0 && method.getReturnType() != null,
        "Only getters allowed here");

    return getPropertyNameFromGetterName(method);
  }

  public static String getPropertyNameFromGetterName(Method method) {
    Preconditions.checkArgument(method != null, "Method required");
    String name = method.getName();
    if (name.startsWith("get")) {
      return name.substring(3, 4).toLowerCase() + name.substring(4);
    } else if (name.startsWith("is")) {
      return name.substring(2, 3).toLowerCase() + name.substring(3);
    } else {
      throw new IllegalArgumentException(
          "Method "
              + name
              + " does not conform to getters naming convention -- must start with either \"get\" or \"is\"");
    }
  }

  public MethodCapturer getMethodCapturer() {
    if (methodCapturer == null) {
      methodCapturer = methodCapturerSupplier.get();
    }
    return methodCapturer;
  }
}
