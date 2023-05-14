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

import org.summerb.easycrud.api.EasyCrudDao;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.impl.EasyCrudServicePluggableImpl;
import org.summerb.easycrud.scaffold.api.CallableMethod;
import org.summerb.easycrud.scaffold.api.ScaffoldedMethodFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class EasyCrudServiceScaffoldedImpl
		extends EasyCrudServicePluggableImpl<Object, HasId<Object>, EasyCrudDao<Object, HasId<Object>>>
		implements java.lang.reflect.InvocationHandler {

	protected ScaffoldedMethodFactory scaffoldedMethodFactory;
	protected Class<?> interfaceType;

	/**
	 * We'd better cache method callers so that we don't need to do rather expensive
	 * reflection-based checks on every invocation
	 */
	protected LoadingCache<Method, CallableMethod> methodCallers;

	public EasyCrudServiceScaffoldedImpl(ScaffoldedMethodFactory scaffoldedMethodFactory, Class<?> interfaceType) {
		this.scaffoldedMethodFactory = scaffoldedMethodFactory;
		this.interfaceType = interfaceType;

		methodCallers = CacheBuilder.newBuilder().build(methodCallerFactory);
	}

	@SuppressWarnings("unchecked")
	public static <T> T createImpl(Class<T> interfaceType, ScaffoldedMethodFactory scaffoldedMethodFactory) {
		ClassLoader cl = interfaceType.getClassLoader();
		Class<?>[] target = new Class<?>[] { interfaceType };
		EasyCrudServiceScaffoldedImpl proxyImpl = new EasyCrudServiceScaffoldedImpl(scaffoldedMethodFactory,
				interfaceType);
		return (T) Proxy.newProxyInstance(cl, target, proxyImpl);
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
				return method.invoke(this, args);
			}

			return methodCallers.get(method).call(args);
		} catch (InvocationTargetException t) {
			throw t.getCause();
		}
	}

	protected void invokeDefault(Object proxy, Method method, Object[] args) {
		throw new IllegalArgumentException("default methods invocation is not supported yet");
	}

	protected CacheLoader<Method, CallableMethod> methodCallerFactory = new CacheLoader<Method, CallableMethod>() {
		@Override
		public CallableMethod load(Method key) throws Exception {
			if (EasyCrudService.class.equals(key.getDeclaringClass())) {
				return new CallableMethodLocalImpl(key);
			}

			// NOTE: Here we assume that all custom methods can be implemented by
			// scaffoldedMethodFactory
			return scaffoldedMethodFactory.create(key);
		}
	};

	public class CallableMethodLocalImpl implements CallableMethod {
		protected Method method;

		public CallableMethodLocalImpl(Method method) {
			this.method = method;
		}

		@Override
		public Object call(Object[] args) throws Exception {
			return method.invoke(EasyCrudServiceScaffoldedImpl.this, args);
		}
	}
}
