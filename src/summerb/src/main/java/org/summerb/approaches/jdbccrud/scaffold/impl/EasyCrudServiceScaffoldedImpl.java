package org.summerb.approaches.jdbccrud.scaffold.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.summerb.approaches.jdbccrud.api.EasyCrudDao;
import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.impl.EasyCrudServicePluggableImpl;

public class EasyCrudServiceScaffoldedImpl
		extends EasyCrudServicePluggableImpl<Object, HasId<Object>, EasyCrudDao<Object, HasId<Object>>>
		implements java.lang.reflect.InvocationHandler {

	@SuppressWarnings("unchecked")
	public static <T> T createImpl(Class<T> interfaceType) {
		ClassLoader cl = interfaceType.getClassLoader();
		Class<?>[] target = new Class<?>[] { interfaceType };
		EasyCrudServiceScaffoldedImpl proxyImpl = new EasyCrudServiceScaffoldedImpl();
		return (T) Proxy.newProxyInstance(cl, target, proxyImpl);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return method.invoke(this, args);
	}

}
