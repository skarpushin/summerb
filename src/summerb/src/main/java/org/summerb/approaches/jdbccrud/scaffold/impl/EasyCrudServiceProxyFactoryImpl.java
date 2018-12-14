package org.summerb.approaches.jdbccrud.scaffold.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.approaches.jdbccrud.scaffold.api.EasyCrudServiceProxyFactory;
import org.summerb.approaches.jdbccrud.scaffold.api.ScaffoldedMethodFactory;

public class EasyCrudServiceProxyFactoryImpl implements EasyCrudServiceProxyFactory {
	private ScaffoldedMethodFactory scaffoldedMethodFactory;

	@Override
	public <TService> TService createImpl(Class<TService> serviceInterface) {
		return EasyCrudServiceScaffoldedImpl.createImpl(serviceInterface, scaffoldedMethodFactory);
	}

	public ScaffoldedMethodFactory getScaffoldQueryImplFactory() {
		return scaffoldedMethodFactory;
	}

	@Autowired(required = false)
	public void setScaffoldQueryImplFactory(ScaffoldedMethodFactory scaffoldedMethodFactory) {
		this.scaffoldedMethodFactory = scaffoldedMethodFactory;
	}
}
