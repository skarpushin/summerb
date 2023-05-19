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
package org.summerb.easycrud.scaffold.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.scaffold.api.CallableMethod;
import org.summerb.easycrud.scaffold.api.ScaffoldedMethodFactory;
import org.summerb.easycrud.scaffold.api.ScaffoldedQuery;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class EasyCrudServiceScaffoldedImpl implements java.lang.reflect.InvocationHandler {

  protected ScaffoldedMethodFactory scaffoldedMethodFactory;
  protected Class<?> interfaceType;
  protected EasyCrudService<?, HasId<?>> actual;

  /**
   * We'd better cache method callers so that we don't need to do rather expensive reflection-based
   * checks on every invocation
   */
  protected LoadingCache<Method, CallableMethod> methodCallers;

  protected EasyCrudServiceScaffoldedImpl(
      ScaffoldedMethodFactory scaffoldedMethodFactory,
      Class<?> interfaceType,
      EasyCrudService<?, HasId<?>> actual) {
    Preconditions.checkArgument(interfaceType != null, "interfaceType required");
    Preconditions.checkArgument(actual != null, "actual required");

    this.scaffoldedMethodFactory = scaffoldedMethodFactory;
    this.interfaceType = interfaceType;
    this.actual = actual;

    methodCallers = CacheBuilder.newBuilder().build(methodCallerFactory);
  }

  @SuppressWarnings("unchecked")
  public static <TId, TDto extends HasId<TId>, TService extends EasyCrudService<TId, TDto>>
      TService createImpl(
          Class<TService> interfaceType,
          EasyCrudService<TId, TDto> actualImpl,
          ScaffoldedMethodFactory scaffoldedMethodFactory) {

    Preconditions.checkArgument(interfaceType != null, "interfaceType required");
    Preconditions.checkArgument(actualImpl != null, "actualImpl required");

    ClassLoader cl = interfaceType.getClassLoader();
    Class<?>[] target = new Class<?>[] {interfaceType};
    EasyCrudServiceScaffoldedImpl proxyImpl =
        new EasyCrudServiceScaffoldedImpl(
            scaffoldedMethodFactory, interfaceType, (EasyCrudService<?, HasId<?>>) actualImpl);
    return (TService) Proxy.newProxyInstance(cl, target, proxyImpl);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      CallableMethod ret = methodCallers.getIfPresent(method);
      if (ret != null) {
        return ret.call(args);
      }

      if (method.isDefault()) {
        invokeDefault(proxy, method, args);
      }

      if (Object.class.equals(method.getDeclaringClass())) {
        return method.invoke(actual, args);
      }

      return methodCallers.get(method).call(args);
    } catch (InvocationTargetException t) {
      throw t.getCause();
    }
  }

  protected void invokeDefault(Object proxy, Method method, Object[] args) {
    throw new IllegalArgumentException(
        "default methods invocation is not supported yet. In Java 17 this will be easily implemented");
  }

  protected CacheLoader<Method, CallableMethod> methodCallerFactory =
      new CacheLoader<Method, CallableMethod>() {
        @Override
        public CallableMethod load(Method key) throws Exception {
          if (EasyCrudService.class.equals(key.getDeclaringClass())) {
            return new CallableMethodLocalImpl(key);
          }

          if (key.isAnnotationPresent(ScaffoldedQuery.class)) {
            Preconditions.checkState(
                scaffoldedMethodFactory != null,
                "scaffoldedMethodFactory is required if you want to call scaffoldedmethods");
            return scaffoldedMethodFactory.create(key);
          }

          throw new IllegalStateException(
              "Cannot invoke method " + key.getName() + " -- case not supported");
        }
      };

  public class CallableMethodLocalImpl implements CallableMethod {
    protected Method method;

    public CallableMethodLocalImpl(Method method) {
      this.method = method;
    }

    @Override
    public Object call(Object[] args) throws Exception {
      return method.invoke(actual, args);
    }
  }
}
