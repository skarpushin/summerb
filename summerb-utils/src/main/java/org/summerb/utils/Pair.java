package org.summerb.utils;

import java.util.Map;

public class Pair<K, V> implements Map.Entry<K, V> {
	K key;
	private V value;

	public Pair(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}

	public static <K, V> Pair<K, V> of(K key, V value) {
		return new Pair<>(key, value);
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		V oldValue = this.value;
		this.value = value;
		return oldValue;
	}
}