package org.summerb.approaches.jdbccrud.mvc.filter;

import java.util.Map;

import org.summerb.approaches.jdbccrud.api.query.Query;

public interface FilteringParamsToQueryConverter {
	Query convert(Map<String, FilteringParam> filterParams, Class<?> dtoClazz);
}
