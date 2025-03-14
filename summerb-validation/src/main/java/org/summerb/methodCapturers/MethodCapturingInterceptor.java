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
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

public class MethodCapturingInterceptor {

  @RuntimeType
  public static Object intercept(@This MethodCapturer capturer, @Origin Method method) {
    capturer.set__Method(method);
    return null;

    // NOTE: You'd think that we'd have to return different default value for different return types
    // (as commented-out below) but it doesn't matter at all. We can just return null regardless of
    // return type and code will work just fine. See MethodCapturingInterceptorTest -- it proves

  }
}
