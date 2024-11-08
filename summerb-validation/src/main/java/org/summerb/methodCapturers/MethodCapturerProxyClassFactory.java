/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
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
  MethodCapturer buildProxyFor(Class<?> clazz);
}
