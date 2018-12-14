package org.summerb.approaches.jdbccrud.scaffold.api;

import org.summerb.approaches.jdbccrud.api.EasyCrudService;

/**
 * Factory for Proxies of custom sub-interfaces of {@link EasyCrudService}
 * 
 * @author sergeyk
 *
 */
public interface EasyCrudServiceProxyFactory {

	<TService> TService createImpl(Class<TService> serviceInterface);

}
