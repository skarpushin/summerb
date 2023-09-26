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
package org.summerb.easycrud.impl.dao;

import java.sql.Types;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.summerb.easycrud.api.ParameterSourceBuilder;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/** @author sergey.karpushin */
public class ParameterSourceBuilderBeanPropImpl<TRow> implements ParameterSourceBuilder<TRow> {
  protected SqlTypeOverrides overrides;

  protected static final SqlTypeOverride NOT_FOUND = new SqlTypeOverride();

  public ParameterSourceBuilderBeanPropImpl() {
    this.overrides = new SqlTypeOverridesDefaultImpl();
  }

  public ParameterSourceBuilderBeanPropImpl(SqlTypeOverrides overrides) {
    Preconditions.checkNotNull(overrides, "override required");
    this.overrides = overrides;
  }

  @Override
  public SqlParameterSource buildParameterSource(TRow row) {
    BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(row);

    /** We're going an extra mile here in attempt to optimize performance */
    LoadingCache<String, SqlTypeOverride> fieldNameToOverrideCache =
        CacheBuilder.newBuilder().build(buildOverrideLoader(beanWrapper));

    return new BeanPropertySqlParameterSource(row) {
      @Override
      public Object getValue(String paramName) throws IllegalArgumentException {
        var rawValue = super.getValue(paramName);
        if (rawValue == null) {
          return null;
        }

        SqlTypeOverride override = fieldNameToOverrideCache.getUnchecked(paramName);
        if (override != NOT_FOUND) {
          return override.convert(rawValue);
        }

        return rawValue;
      }

      @Override
      public int getSqlType(String paramName) {
        SqlTypeOverride override = fieldNameToOverrideCache.getUnchecked(paramName);
        if (override == NOT_FOUND) {
          return super.getSqlType(paramName);
        }

        return override.getSqlType();
      }
    };
  }

  protected CacheLoader<String, SqlTypeOverride> buildOverrideLoader(
      BeanWrapper beanWrapper) {
    return new CacheLoader<>() {
      @Override
      public SqlTypeOverride load(String fieldName) throws Exception {
        Class<?> fieldType = beanWrapper.getPropertyType(fieldName);

        SqlTypeOverride override = overrides.findOverrideForClass(fieldType);
        if (override != null) {
          return override;
        }

        return NOT_FOUND;
      }
    };
  }

  public SqlTypeOverrides getOverrides() {
    return overrides;
  }

  public void setOverrides(SqlTypeOverrides overrides) {
    this.overrides = overrides;
  }
}
