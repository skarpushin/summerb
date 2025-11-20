package org.summerb.easycrud.dao;

import static org.summerb.easycrud.dao.ParameterSourceBuilderBeanPropImpl.COLUMN_NOT_MAPPED;
import static org.summerb.easycrud.dao.ParameterSourceBuilderBeanPropImpl.NO_OVERRIDE;

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
  protected final String[] propertyNames;

  public BeanPropertySqlParameterSourceEx(
      TRow row,
      LoadingCache<String, String> columnNameToFieldNameCache,
      LoadingCache<String, SqlTypeOverride> fieldNameToOverrideCache,
      String[] propertyNames) {
    super(row);
    this.columnNameToFieldNameCache = columnNameToFieldNameCache;
    this.fieldNameToOverrideCache = fieldNameToOverrideCache;
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
    return !COLUMN_NOT_MAPPED.equals(columnNameToFieldNameCache.getUnchecked(columnName));
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

    SqlTypeOverride override = fieldNameToOverrideCache.getUnchecked(fieldName);
    if (NO_OVERRIDE.equals(override)) {
      return TYPE_UNKNOWN;
    }

    return override.getSqlType();
  }
}
