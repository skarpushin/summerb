/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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
 * A helper interface which implementation will be created as a proxy (mix-in) to original POJO
 * (ROW) class. Instance is supposed to be obtained via {@link MethodCapturerProxyClassFactory}
 *
 * @author Sergey Karpushin
 */
public interface MethodCapturer {
  Method get__Method();

  void set__Method(Method methodName);
}
