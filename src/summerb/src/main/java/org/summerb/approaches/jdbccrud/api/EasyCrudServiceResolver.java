package org.summerb.approaches.jdbccrud.api;

import org.summerb.approaches.jdbccrud.api.EasyCrudService;

public interface EasyCrudServiceResolver {
	@SuppressWarnings("rawtypes")
	EasyCrudService resolveByEntityType(String entityName);
	
	@SuppressWarnings("rawtypes")
	EasyCrudService resolveByDtoClass(Class<?> entityClass);
}
