package org.summerb.microservices.properties.impl.dao;

import java.util.Map.Entry;

public class AliasEntry implements Entry<String, Long> {
	private String key;
	private Long value;

	public AliasEntry(String key, Long value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public Long getValue() {
		return value;
	}

	@Override
	public Long setValue(Long value) {
		return this.value = value;
	}

}