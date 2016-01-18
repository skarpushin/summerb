package org.summerb.utils.collection;

public interface ItemConverter<F, T> {
	T convert(F from);
}
