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
package org.summerb.security.elevation.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.util.StringUtils;
import org.summerb.security.elevation.api.ElevationRunner;

import com.google.common.base.Preconditions;

/**
 * Sometimes it's useful to have a bean that is always elevated to a certain permissions level.
 * Create this proxy to "wrap" actual bean
 *
 * @author sergeyk
 */
public class ElevationProxy implements java.lang.reflect.InvocationHandler {
  private Object impl;
  private ElevationRunner elevationRunner;

  private ElevationProxy(Object impl, ElevationRunner elevationRunner) {
    this.impl = impl;
    this.elevationRunner = elevationRunner;
  }

  public static <T> T create(String interfaceTypeStr, T impl, ElevationRunner elevationRunner) {
    try {
      Preconditions.checkArgument(StringUtils.hasText(interfaceTypeStr));
      Class<?> interfaceType = Class.forName(interfaceTypeStr);
      return doCreate(interfaceType, impl, elevationRunner);
    } catch (Throwable t) {
      throw new RuntimeException("Failed to create elevated proxy for " + interfaceTypeStr, t);
    }
  }

  public static <T> T create(Class<T> interfaceType, T impl, ElevationRunner elevationRunner) {
    try {
      return doCreate(interfaceType, impl, elevationRunner);
    } catch (Throwable t) {
      throw new RuntimeException("Failed to create elevated proxy for " + interfaceType, t);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> T doCreate(Class<?> interfaceType, T impl, ElevationRunner elevationRunner) {
    Preconditions.checkArgument(impl != null);
    Preconditions.checkArgument(elevationRunner != null);
    ClassLoader cl = interfaceType.getClassLoader();
    Class<?>[] target = new Class<?>[] {interfaceType};
    ElevationProxy proxyImpl = new ElevationProxy(impl, elevationRunner);
    return (T) Proxy.newProxyInstance(cl, target, proxyImpl);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      return elevationRunner.callElevated(() -> method.invoke(impl, args));
    } catch (InvocationTargetException t) {
      throw t.getCause();
    }
  }
}
