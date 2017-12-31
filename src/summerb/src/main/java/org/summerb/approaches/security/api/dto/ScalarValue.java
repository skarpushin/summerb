package org.summerb.approaches.security.api.dto;

import java.io.Serializable;

public class ScalarValue<T> implements Serializable {
	private static final long serialVersionUID = 552482292114371317L;

	private T value;

	public static <T1> ScalarValue<T1> forV(T1 value) {
		ScalarValue<T1> ret = new ScalarValue<T1>();
		ret.value = value;
		return ret;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
