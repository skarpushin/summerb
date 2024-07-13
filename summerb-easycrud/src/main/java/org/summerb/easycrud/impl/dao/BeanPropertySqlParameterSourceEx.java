package org.summerb.easycrud.impl.dao;

import static org.summerb.easycrud.impl.dao.ParameterSourceBuilderBeanPropImpl.COLUMN_NOT_MAPPED;

import com.google.common.cache.LoadingCache;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

/**
 * A heavily optimized alternative to {@link BeanPropertySqlParameterSource}.
 *
 * <p>Cache functionality injected in constructor provided in {@link
 * ParameterSourceBuilderBeanPropImpl}
 *
 * @param <TRow> row type
 */
public class BeanPropertySqlParameterSourceEx<TRow> extends BeanPropertySqlParameterSource {
  protected LoadingCache<String, String> columnNameToFieldNameCache;
  protected LoadingCache<String, SqlTypeOverride> fieldNameToOverrideCache;
  protected LoadingCache<String, Boolean> columnNameToHasValueCache;
  protected LoadingCache<String, Integer> fieldNameToSqlType;
  protected final String[] propertyNames;

  public BeanPropertySqlParameterSourceEx(
      TRow row,
      LoadingCache<String, String> columnNameToFieldNameCache,
      LoadingCache<String, Boolean> columnNameToHasValueCache,
      LoadingCache<String, SqlTypeOverride> fieldNameToOverrideCache,
      LoadingCache<String, Integer> fieldNameToSqlType,
      String[] propertyNames) {
    super(row);
    this.columnNameToFieldNameCache = columnNameToFieldNameCache;
    this.fieldNameToOverrideCache = fieldNameToOverrideCache;
    this.columnNameToHasValueCache = columnNameToHasValueCache;
    this.fieldNameToSqlType = fieldNameToSqlType;
    this.propertyNames = propertyNames;
  }

  @Override
  public String[] getParameterNames() {
    return propertyNames;
  }

  @Override
  public String[] getReadablePropertyNames() {
    return propertyNames;
  }

  @Override
  public boolean hasValue(String columnName) {
    return columnNameToHasValueCache.getUnchecked(columnName);
  }

  @Override
  public Object getValue(String columnName) throws IllegalArgumentException {
    String fieldName = columnNameToFieldNameCache.getUnchecked(columnName);
    if (COLUMN_NOT_MAPPED.equals(fieldName)) {
      return null;
    }

    Object rawValue = super.getValue(fieldName);
    if (rawValue == null) {
      return null;
    }

    SqlTypeOverride override = fieldNameToOverrideCache.getUnchecked(fieldName);
    if (override != ParameterSourceBuilderBeanPropImpl.NO_OVERRIDE
        && override.isConversionRequired()) {
      return override.convert(rawValue);
    }

    return rawValue;
  }

  @Override
  public int getSqlType(String columnName) {
    String fieldName = columnNameToFieldNameCache.getUnchecked(columnName);
    if (COLUMN_NOT_MAPPED.equals(fieldName)) {
      return TYPE_UNKNOWN;
    }

    return fieldNameToSqlType.getUnchecked(fieldName);
  }
}
