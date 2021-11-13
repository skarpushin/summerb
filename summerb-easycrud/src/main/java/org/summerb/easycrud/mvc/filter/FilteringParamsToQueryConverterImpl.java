/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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
package org.summerb.easycrud.mvc.filter;

import java.util.Map;
import java.util.Map.Entry;

import org.summerb.easycrud.api.query.Query;

/**
 * 
 * @author sergeyk
 * 
 *         NOTE: Yes, I know, according to OCP it's better to use
 *         delegating/aggregating approach, but don't want to over-engineer. In
 *         behavior needs to be changed one can override this class or provide
 *         other impl
 */
public class FilteringParamsToQueryConverterImpl implements FilteringParamsToQueryConverter {

	@Override
	public Query convert(Map<String, FilteringParam> filterParams, Class<?> dtoClazz) {
		if (filterParams == null || filterParams.isEmpty()) {
			return null;
		}

		try {
			Query ret = Query.n();
			for (Entry<String, FilteringParam> entry : filterParams.entrySet()) {
				String fname = entry.getKey();
				String[] values = entry.getValue().getValues();
				Class<?> type = getFieldType(dtoClazz, fname);
				boolean isStringType = String.class.equals(type);
				boolean isNumericType = int.class.equals(type) || long.class.equals(type) || Integer.class.equals(type)
						|| Long.class.equals(type);
				boolean isBooleanType = boolean.class.equals(type) || Boolean.class.equals(type);

				String command = entry.getValue().getCommand();
				addFilteringParamToQUery(fname, command, values, type, isStringType, isNumericType, isBooleanType, ret);
			}
			return ret;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to parse filtering params", t);
		}
	}

	protected void addFilteringParamToQUery(String fname, String command, String[] values, Class<?> type,
			boolean isStringType, boolean isNumericType, boolean isBooleanType, Query ret) {
		switch (command) {
		case FilteringParam.CMD_BETWEEN:
			ret.between(fname, Long.parseLong(values[0]), Long.parseLong(values[1]));
			break;
		case FilteringParam.CMD_CONTAIN:
			ret.contains(fname, values[0]);
			break;
		case FilteringParam.CMD_EQUALS:
			if (isNumericType) {
				ret.eq(fname, Long.parseLong(values[0]));
			} else if (isStringType) {
				ret.eq(fname, values[0]);
			} else {
				throw new IllegalArgumentException("Field type " + type + " is not supported for predicate " + command);
			}
			break;
		case FilteringParam.CMD_GREATER:
			ret.between(fname, Long.parseLong(values[0]) + 1, Long.MAX_VALUE);
			break;
		case FilteringParam.CMD_GREATER_OR_EQUAL:
			ret.between(fname, Long.parseLong(values[0]), Long.MAX_VALUE);
			break;
		case FilteringParam.CMD_IN:
			if (isNumericType) {
				ret.in(fname, convertToLongs(values));
			} else if (isStringType) {
				ret.in(fname, values);
			} else {
				throw new IllegalArgumentException("Field type " + type + " is not supported for predicate " + command);
			}
			break;
		case FilteringParam.CMD_LESS:
			ret.between(fname, Long.MIN_VALUE, Long.parseLong(values[0]) + 1);
			break;
		case FilteringParam.CMD_LESS_OR_EQUAL:
			ret.between(fname, Long.MIN_VALUE, Long.parseLong(values[0]));
			break;
		case FilteringParam.CMD_NOT_BETWEEN:
			ret.notBetween(fname, Long.parseLong(values[0]), Long.parseLong(values[1]));
			break;
		case FilteringParam.CMD_NOT_CONTAIN:
			ret.notContains(fname, values[0]);
			break;
		case FilteringParam.CMD_NOT_EQUALS:
			if (isNumericType) {
				ret.ne(fname, Long.parseLong(values[0]));
			} else if (isStringType) {
				ret.ne(fname, values[0]);
			} else if (isBooleanType) {
				ret.ne(fname, Boolean.parseBoolean(values[0]));
			} else {
				throw new IllegalArgumentException("Field type " + type + " is not supported for predicate " + command);
			}
			break;
		case FilteringParam.CMD_NOT_IN:
			if (isNumericType) {
				ret.notIn(fname, convertToLongs(values));
			} else if (isStringType) {
				ret.notIn(fname, values);
			} else {
				throw new IllegalArgumentException("Field type " + type + " is not supported for predicate " + command);
			}
			break;
		default:
			throw new IllegalArgumentException("Unsupported filtering predicate: " + command);
		}
	}

	private static Class<?> getFieldType(Class<?> clazz, String fname) {
		try {
			return clazz.getMethod("get" + fname.substring(0, 1).toUpperCase() + fname.substring(1)).getReturnType();
		} catch (Throwable t) {
			throw new RuntimeException("Failed to resolve field type", t);
		}
	}

	private static Long[] convertToLongs(String[] values) {
		Long[] ret = new Long[values.length];
		for (int i = 0; i < values.length; i++) {
			ret[i] = Long.parseLong(values[i]);
		}
		return ret;
	}

}
