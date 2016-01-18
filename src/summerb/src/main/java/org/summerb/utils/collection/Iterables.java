package org.summerb.utils.collection;

import java.util.LinkedList;
import java.util.List;

public abstract class Iterables {
	public static <T, V> T findItem(Iterable<T> items, ParametrizedItemMatcher<T, V> matcher, V param) {
		for (T item : items) {
			if (matcher.isMatch(item, param)) {
				return item;
			}
		}
		return null;
	}

	public static <T, V> List<T> findItems(Iterable<T> items, ParametrizedItemMatcher<T, V> matcher, V param) {
		List<T> ret = new LinkedList<T>();
		for (T item : items) {
			if (matcher.isMatch(item, param)) {
				ret.add(item);
			}
		}
		return ret;
	}

	public static <F, T> List<T> convert(List<F> from, ItemConverter<F, T> converter) {
		List<T> ret = new LinkedList<T>();
		for (F item : from) {
			ret.add(converter.convert(item));
		}
		return ret;
	}
}
