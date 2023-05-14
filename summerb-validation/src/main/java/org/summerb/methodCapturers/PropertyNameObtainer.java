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
