/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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
package org.summerb.easycrud.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.summerb.easycrud.EasyCrudService;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.rest.model.FilteringParam;
import org.summerb.easycrud.row.HasId;

/**
 * @author sergeyk
 *     <p>NOTE: Yes, I know, according to OCP it's better to use delegating/aggregating approach,
 *     but don't want to over-engineer. In behavior needs to be changed one can override this class
 *     or provide other impl
 */
public class FilteringParamsToQueryConverterImpl<TId, TRow extends HasId<TId>>
    implements FilteringParamsToQueryConverter<TId, TRow> {

  private final EasyCrudService<TId, TRow> service;

  public FilteringParamsToQueryConverterImpl(EasyCrudService<TId, TRow> service) {
    this.service = service;
  }

  @Override
  public Query<TId, TRow> convert(Map<String, FilteringParam> filterParams) {
    if (filterParams == null || filterParams.isEmpty()) {
      return null;
    }

    try {
      Query<TId, TRow> ret = service.query();
      for (Entry<String, FilteringParam> entry : filterParams.entrySet()) {
        String fname = entry.getKey();
        String[] values = entry.getValue().getValues();
        Class<?> type = getFieldType(service.getRowClass(), fname);
        boolean isStringType = String.class.equals(type);
        boolean isNumericType =
            int.class.equals(type)
                || long.class.equals(type)
                || Integer.class.equals(type)
                || Long.class.equals(type);
        boolean isBooleanType = boolean.class.equals(type) || Boolean.class.equals(type);

        String command = entry.getValue().getCommand();
        addFilteringParamToQuery(
            fname, command, values, type, isStringType, isNumericType, isBooleanType, ret);
      }
      return ret;
    } catch (Throwable t) {
      throw new RuntimeException("Failed to parse filtering params", t);
    }
  }

  protected void addFilteringParamToQuery(
      String fname,
      String command,
      String[] values,
      Class<?> type,
      boolean isStringType,
      boolean isNumericType,
      boolean isBooleanType,
      Query<TId, TRow> ret) {
    switch (command) {
      case FilteringParam.CMD_BETWEEN:
        if (isNumericType) {
          ret.between(fname, Long.parseLong(values[0]), Long.parseLong(values[1]));
        } else {
          ret.between(fname, values[0], values[1]);
        }
        break;
      case FilteringParam.CMD_CONTAIN:
        ret.contains(fname, values[0]);
        break;
      case FilteringParam.CMD_EQUALS:
        ret.eq(fname, values[0]);
        break;
      case FilteringParam.CMD_GREATER:
        if (isNumericType) {
          ret.greater(fname, Long.parseLong(values[0]));
        } else {
          ret.greater(fname, values[0]);
        }
        break;
      case FilteringParam.CMD_GREATER_OR_EQUAL:
        ret.ge(fname, Long.parseLong(values[0]));
        break;
      case FilteringParam.CMD_IN:
        if (isNumericType) {
          ret.in(fname, convertToLongs(values));
        } else if (isStringType) {
          ret.in(fname, Arrays.asList(values));
        } else {
          throw new IllegalArgumentException(
              "Field type " + type + " is not supported for predicate " + command);
        }
        break;
      case FilteringParam.CMD_LESS:
        if (isNumericType) {
          ret.less(fname, Long.parseLong(values[0]));
        } else {
          ret.less(fname, values[0]);
        }
        break;
      case FilteringParam.CMD_LESS_OR_EQUAL:
        if (isNumericType) {
          ret.le(fname, Long.parseLong(values[0]));
        } else {
          ret.le(fname, values[0]);
        }
        break;
      case FilteringParam.CMD_NOT_BETWEEN:
        if (isNumericType) {
          ret.notBetween(fname, Long.parseLong(values[0]), Long.parseLong(values[1]));
        } else {
          ret.notBetween(fname, values[0], values[1]);
        }
        break;
      case FilteringParam.CMD_NOT_CONTAIN:
        ret.notContains(fname, values[0]);
        break;
      case FilteringParam.CMD_NOT_EQUALS:
        ret.ne(fname, values[0]);
        break;
      case FilteringParam.CMD_NOT_IN:
        if (isNumericType) {
          ret.notIn(fname, convertToLongs(values));
        } else if (isStringType) {
          ret.notIn(fname, Arrays.asList(values));
        } else {
          throw new IllegalArgumentException(
              "Field type " + type + " is not supported for predicate " + command);
        }
        break;
      default:
        throw new IllegalArgumentException("Unsupported filtering predicate: " + command);
    }
  }

  protected static Class<?> getFieldType(Class<?> clazz, String fname) {
    try {
      return clazz
          .getMethod("get" + fname.substring(0, 1).toUpperCase() + fname.substring(1))
          .getReturnType();
    } catch (Throwable t) {
      throw new RuntimeException("Failed to resolve field type", t);
    }
  }

  protected static List<Long> convertToLongs(String[] values) {
    List<Long> ret = new ArrayList<>(values.length);
    for (String value : values) {
      ret.add(Long.parseLong(value));
    }
    return ret;
  }
}
