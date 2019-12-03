/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
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
