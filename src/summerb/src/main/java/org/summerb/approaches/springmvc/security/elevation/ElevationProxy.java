package org.summerb.approaches.springmvc.security.elevation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.util.StringUtils;
import org.summerb.approaches.springmvc.security.apis.ElevationRunner;

import com.google.common.base.Preconditions;

/**
 * Sometimes it's useful to have a bean that is always elevated to a certain
 * permissions level. Create this proxy to "wrap" actual bean
 * 
 * @author sergeyk
 *
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
		Class<?>[] target = new Class<?>[] { interfaceType };
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
