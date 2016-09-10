package org.summerb.approaches.jdbccrud.api.dto.tools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.summerb.approaches.jdbccrud.api.dto.HasId;

public abstract class EasyCrudDtoUtils {
	private EasyCrudDtoUtils() {
	}

	public static <T, TDto extends HasId<T>> Set<T> enumerateIds(Iterable<TDto> iterable) {
		Set<T> ret = new HashSet<>();
		for (HasId<T> row : iterable) {
			ret.add(row.getId());
		}
		return ret;
	}

	public static <TId, TDto extends HasId<TId>> Map<TId, TDto> toMapById(Iterable<TDto> iterable) {
		Map<TId, TDto> ret = new HashMap<>();
		for (TDto row : iterable) {
			ret.put(row.getId(), row);
		}
		return ret;
	}
}
