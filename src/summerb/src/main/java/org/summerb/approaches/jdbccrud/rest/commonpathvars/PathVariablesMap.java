package org.summerb.approaches.jdbccrud.rest.commonpathvars;

import java.util.HashMap;
import java.util.Map;

public class PathVariablesMap {
	private Map<String, Object> map = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) map.get(key);
	}

	protected void put(String paramName, Object value) {
		map.put(paramName, value);
	}
}
