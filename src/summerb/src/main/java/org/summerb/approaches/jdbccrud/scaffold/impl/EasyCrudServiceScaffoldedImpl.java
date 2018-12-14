package org.summerb.approaches.jdbccrud.scaffold.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.summerb.approaches.jdbccrud.api.EasyCrudDao;
import org.summerb.approaches.jdbccrud.api.EasyCrudService;
import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.impl.EasyCrudServicePluggableImpl;
import org.summerb.approaches.jdbccrud.scaffold.api.CallableMethod;
import org.summerb.approaches.jdbccrud.scaffold.api.ScaffoldedMethodFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class EasyCrudServiceScaffoldedImpl
		extends EasyCrudServicePluggableImpl<Object, HasId<Object>, EasyCrudDao<Object, HasId<Object>>>
		implements java.lang.reflect.InvocationHandler {

	private ScaffoldedMethodFactory scaffoldedMethodFactory;

	/**
	 * We'd better cache method callers so that we don't need to do rather expensive
	 * reflection-based checks on every invocation
	 */
	private LoadingCache<Method, CallableMethod> methodCallers;

	@SuppressWarnings("unchecked")
	public static <T> T createImpl(Class<T> interfaceType,
			ScaffoldedMethodFactory scaffoldedMethodFactory) {
		ClassLoader cl = interfaceType.getClassLoader();
		Class<?>[] target = new Class<?>[] { interfaceType };
		EasyCrudServiceScaffoldedImpl proxyImpl = new EasyCrudServiceScaffoldedImpl(
				scaffoldedMethodFactory);
		return (T) Proxy.newProxyInstance(cl, target, proxyImpl);
	}

	public EasyCrudServiceScaffoldedImpl(
			ScaffoldedMethodFactory scaffoldedMethodFactory) {
		this.scaffoldedMethodFactory = scaffoldedMethodFactory;
		methodCallers = CacheBuilder.newBuilder().build(methodCallerFactory);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			if (scaffoldedMethodFactory == null) {
				// Since scaffoldedMethodFactory is null, we're not expecting any
				// ScaffoldedQuery methods
				return method.invoke(this, args);
			}

			return methodCallers.get(method).call(args);
		} catch (InvocationTargetException t) {
			throw t.getCause();
		}
	}

	private CacheLoader<Method, CallableMethod> methodCallerFactory = new CacheLoader<Method, CallableMethod>() {
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
		private Method method;

		public CallableMethodLocalImpl(Method method) {
			this.method = method;
		}

		@Override
		public Object call(Object[] args) throws Exception {
			return method.invoke(EasyCrudServiceScaffoldedImpl.this, args);
		}
	}
}
