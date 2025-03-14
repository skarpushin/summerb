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
package org.summerb.easycrud.impl.dao;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.ParameterSourceBuilder;

/**
 * This builder serves 3 purposes:
 *
 * <p>1. Provide extension point for parameter source builders
 *
 * <p>2. Support SqlTypeOverride for fields that needs this
 *
 * <p>3. And last but not least - heavily optimize compared to Spring's default implementation which
 * spends too much time again and again to resolve same data. So here we're using caches a lot
 *
 * @param <TRow> row type
 * @author sergey.karpushin
 */
public class ParameterSourceBuilderBeanPropImpl<TRow> implements ParameterSourceBuilder<TRow> {
  protected final Logger log = LoggerFactory.getLogger(getClass());

  protected static final SqlTypeOverride NO_OVERRIDE = new SqlTypeOverride();
  protected static final String COLUMN_NOT_MAPPED = "COLUMN_NOT_MAPPED";

  protected SqlTypeOverrides overrides;
  protected BeanWrapper beanWrapper;
  protected Class<TRow> rowClazz;
  protected Set<String> readableProperties;
  protected Map<String, String> mapLowerCaseToPropertyName;
  protected LoadingCache<String, String> columnNameToFieldNameCache;
  protected LoadingCache<String, SqlTypeOverride> fieldNameToOverrideCache;
  protected LoadingCache<String, Boolean> columnNameToHasValueCache;
  protected LoadingCache<String, Integer> fieldNameToSqlType;
  protected String[] propertyNames;

  public ParameterSourceBuilderBeanPropImpl(SqlTypeOverrides overrides, Class<TRow> rowClazz) {
    Preconditions.checkNotNull(rowClazz, "rowClazz required");
    Preconditions.checkNotNull(overrides, "override required");
    this.overrides = overrides;
    this.rowClazz = rowClazz;

    beanWrapper = buildBeanWrapper(rowClazz);
    fieldNameToOverrideCache = buildFieldNameToOverrideCache();
    columnNameToHasValueCache = buildColumnNameToHasValueCache();
    columnNameToFieldNameCache = buildColumnNameToFieldNameCache();
    fieldNameToSqlType = buildFieldNameToSqlTypeCache();

    readableProperties = buildReadableProperties();
    mapLowerCaseToPropertyName =
        readableProperties.stream().collect(Collectors.toMap(String::toLowerCase, v -> v));
    propertyNames = StringUtils.toStringArray(readableProperties);
  }

  protected BeanWrapper buildBeanWrapper(Class<TRow> rowClazz) {
    try {
      return PropertyAccessorFactory.forBeanPropertyAccess(
          rowClazz.getDeclaredConstructor().newInstance());
    } catch (Exception e) {
      throw new RuntimeException(
          "Failed to initialize default bean wrapper for row class " + rowClazz, e);
    }
  }

  protected HashSet<String> buildReadableProperties() {
    HashSet<String> ret = new HashSet<>();
    PropertyDescriptor[] props = this.beanWrapper.getPropertyDescriptors();
    for (PropertyDescriptor pd : props) {
      if (this.beanWrapper.isReadableProperty(pd.getName())) {
        ret.add(pd.getName());
      }
    }
    return ret;
  }

  protected LoadingCache<String, Integer> buildFieldNameToSqlTypeCache() {
    return CacheBuilder.newBuilder().build(buildFieldNameToSqlTypeCacheLoader());
  }

  protected LoadingCache<String, String> buildColumnNameToFieldNameCache() {
    return CacheBuilder.newBuilder().build(buildColumnNameToFieldNameCacheLoader());
  }

  protected LoadingCache<String, Boolean> buildColumnNameToHasValueCache() {
    return CacheBuilder.newBuilder().build(buildColumnNameToHasValueCacheLoader());
  }

  protected LoadingCache<String, SqlTypeOverride> buildFieldNameToOverrideCache() {
    return CacheBuilder.newBuilder().build(buildFieldNameToOverrideCacheLoader());
  }

  @Override
  public SqlParameterSource buildParameterSource(TRow row) {
    return new BeanPropertySqlParameterSourceEx<>(
        row,
        columnNameToFieldNameCache,
        columnNameToHasValueCache,
        fieldNameToOverrideCache,
        fieldNameToSqlType,
        propertyNames);
  }

  protected CacheLoader<? super String, String> buildColumnNameToFieldNameCacheLoader() {
    return new CacheLoader<>() {
      @Override
      public String load(String column) {
        if (readableProperties.contains(column)) {
          return column;
        }

        String lowerCaseName = column.toLowerCase();
        if (readableProperties.contains(lowerCaseName)) {
          return lowerCaseName;
        }

        String propertyName = JdbcUtils.convertUnderscoreNameToPropertyName(column);
        if (readableProperties.contains(propertyName)) {
          return propertyName;
        }

        String fieldName = mapLowerCaseToPropertyName.get(lowerCaseName);
        if (fieldName != null) {
          return fieldName;
        }

        log.error("Column {} could not be mapped to field name of {}", column, rowClazz);
        return COLUMN_NOT_MAPPED;
      }
    };
  }

  protected CacheLoader<String, SqlTypeOverride> buildFieldNameToOverrideCacheLoader() {
    return new CacheLoader<>() {
      @Override
      public SqlTypeOverride load(String fieldName) {
        Class<?> fieldType = beanWrapper.getPropertyType(fieldName);

        SqlTypeOverride override = overrides.findOverrideForClass(fieldType);
        if (override != null) {
          return override;
        }

        return NO_OVERRIDE;
      }
    };
  }

  protected CacheLoader<String, Boolean> buildColumnNameToHasValueCacheLoader() {
    return new CacheLoader<>() {
      @Override
      public Boolean load(String columnName) {
        return !COLUMN_NOT_MAPPED.equals(columnNameToFieldNameCache.getUnchecked(columnName));
      }
    };
  }

  private CacheLoader<? super String, Integer> buildFieldNameToSqlTypeCacheLoader() {
    return new CacheLoader<>() {
      @Override
      public Integer load(String fieldName) {
        Class<?> fieldType = beanWrapper.getPropertyType(fieldName);

        SqlTypeOverride override = overrides.findOverrideForClass(fieldType);
        if (override != null) {
          return override.getSqlType();
        }

        return StatementCreatorUtils.javaTypeToSqlParameterType(fieldType);
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
