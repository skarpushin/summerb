package org.summerb.approaches.jdbccrud.api;

import org.summerb.approaches.jdbccrud.api.relations.DataSetLoader;

/**
 * Interface used to resolve services by
 * {@link EasyCrudService#getEntityTypeMessageCode()} or
 * {@link EasyCrudService#getDtoClass()}
 * 
 * Supposed to be used mostly by {@link DataSetLoader}
 * 
 * @author sergeyk
 *
 */
public interface EasyCrudServiceResolver {
	@SuppressWarnings("rawtypes")
	EasyCrudService resolveByEntityType(String entityName);

	@SuppressWarnings("rawtypes")
	EasyCrudService resolveByDtoClass(Class<?> entityClass);
}
