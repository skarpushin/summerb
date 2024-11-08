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
package org.summerb.easycrud.scaffold.impl;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.impl.dao.mysql.EasyCrudDaoInjections;
import org.summerb.easycrud.scaffold.api.CallableMethod;
import org.summerb.easycrud.scaffold.api.ScaffoldedMethodFactory;
import org.summerb.easycrud.scaffold.api.Query;

public class EasyCrudServiceScaffoldedImpl implements java.lang.reflect.InvocationHandler {

  protected ScaffoldedMethodFactory scaffoldedMethodFactory;
  protected Class<?> interfaceType;
  protected EasyCrudService<?, HasId<?>> service;
  protected EasyCrudDaoInjections<?, HasId<?>> optionalDao;

  /**
   * We'd better cache method callers so that we don't need to do rather expensive reflection-based
   * checks on every invocation
   */
  protected LoadingCache<Method, CallableMethod> methodCallers;

  protected EasyCrudServiceScaffoldedImpl(
      ScaffoldedMethodFactory scaffoldedMethodFactory,
      Class<?> interfaceType,
      EasyCrudService<?, HasId<?>> service,
      EasyCrudDaoInjections<?, HasId<?>> optionalDao) {
    Preconditions.checkNotNull(interfaceType, "interfaceType required");
    Preconditions.checkNotNull(service, "service required");

    this.scaffoldedMethodFactory = scaffoldedMethodFactory;
    this.interfaceType = interfaceType;
    this.service = service;
    this.optionalDao = optionalDao;

    methodCallers = CacheBuilder.newBuilder().build(methodCallerFactory);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      CallableMethod ret = methodCallers.getIfPresent(method);
      if (ret != null) {
        return ret.call(args);
      }

      if (Object.class.equals(method.getDeclaringClass())) {
        return method.invoke(service, args);
      }

      if (method.isDefault()) {
        invokeDefault(proxy, method, args);
      }

      return methodCallers.get(method).call(args);
    } catch (InvocationTargetException t) {
      throw t.getCause();
    }
  }

  protected CacheLoader<Method, CallableMethod> methodCallerFactory =
      new CacheLoader<>() {
        @Override
        public CallableMethod load(Method method) {
          if (EasyCrudService.class.equals(method.getDeclaringClass())) {
            return new CallableMethodEasyCrudServiceImpl(method);
          }

          if (method.isAnnotationPresent(Query.class)) {
            Preconditions.checkState(
                optionalDao != null, "Scaffolded methods require DAO to be provided");
            Preconditions.checkState(
                scaffoldedMethodFactory != null,
                "scaffoldedMethodFactory is required if you want to call scaffolded methods");
            return scaffoldedMethodFactory.create(service, optionalDao, method);
          }

          throw new IllegalStateException(
              "Cannot invoke method "
                  + method.getDeclaringClass().getName()
                  + "::"
                  + method.getName()
                  + "(...) -- case not supported");
        }
      };

  public class CallableMethodEasyCrudServiceImpl implements CallableMethod {
    protected Method method;

    public CallableMethodEasyCrudServiceImpl(Method method) {
      this.method = method;
    }

    @Override
    public Object call(Object[] args) throws Exception {
      return method.invoke(service, args);
    }
  }

  protected void invokeDefault(Object proxy, Method method, Object[] args) {
    throw new IllegalArgumentException(
        "default methods invocation is not supported yet. Once we migrate to Java 17+ we can add support for that");
  }
}
