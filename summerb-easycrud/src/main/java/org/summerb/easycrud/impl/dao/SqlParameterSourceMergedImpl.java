/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
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
package org.summerb.easycrud.impl.dao;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.google.common.base.Preconditions;

/**
 * This impl merges several sources to a single parameter source.
 *
 * <p>Comes in handy if needed to for most fields use {@link BeanPropertySqlParameterSource} but use
 * other parameter source for just couple fields. Parameter sources invoked in the order they passed
 * to constructor
 *
 * @author skarpushin
 */
public class SqlParameterSourceMergedImpl implements SqlParameterSource {
  protected final SqlParameterSource[] parameterSources;

  public SqlParameterSourceMergedImpl(SqlParameterSource... parameterSources) {
    Preconditions.checkArgument(parameterSources != null);
    Preconditions.checkArgument(parameterSources.length > 0);
    this.parameterSources = parameterSources;
  }

  @Override
  public boolean hasValue(String paramName) {
    for (SqlParameterSource sqlParameterSource : parameterSources) {
      if (sqlParameterSource.hasValue(paramName)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Object getValue(String paramName) throws IllegalArgumentException {
    IllegalArgumentException lastException =
        new IllegalArgumentException("No such parameter exists: " + paramName);

    for (SqlParameterSource sqlParameterSource : parameterSources) {
      try {
        return sqlParameterSource.getValue(paramName);
      } catch (IllegalArgumentException exc) {
        lastException = exc;
      }
    }

    throw lastException;
  }

  @Override
  public int getSqlType(String paramName) {
    for (SqlParameterSource sqlParameterSource : parameterSources) {
      int ret = sqlParameterSource.getSqlType(paramName);
      if (ret != TYPE_UNKNOWN) {
        return ret;
      }
    }
    return TYPE_UNKNOWN;
  }

  @Override
  public String getTypeName(String paramName) {
    for (SqlParameterSource sqlParameterSource : parameterSources) {
      String ret = sqlParameterSource.getTypeName(paramName);
      if (ret != null) {
        return ret;
      }
    }
    return null;
  }
}
