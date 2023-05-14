/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
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
