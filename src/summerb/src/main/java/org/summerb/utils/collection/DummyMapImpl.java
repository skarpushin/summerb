package org.summerb.utils.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author sergey.karpushin
 */
public class DummyMapImpl<TKey, TValue> implements Map<TKey, TValue> {

	@Override
	public void clear() {
	}

	@Override
	public boolean containsKey(Object arg0) {
		return false;
	}

	@Override
	public boolean containsValue(Object arg0) {
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<TKey, TValue>> entrySet() {
		return null;
	}

	@Override
	public TValue get(Object arg0) {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Set<TKey> keySet() {
		return null;
	}

	@Override
	public TValue put(TKey arg0, TValue arg1) {
		return null;
	}

	@Override
	public void putAll(Map<? extends TKey, ? extends TValue> arg0) {

	}

	@Override
	public TValue remove(Object arg0) {
		return null;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Collection<TValue> values() {
		return null;
	}

}
