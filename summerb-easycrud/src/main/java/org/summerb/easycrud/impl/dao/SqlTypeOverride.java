package org.summerb.easycrud.impl.dao;

import java.sql.Types;
import java.util.function.Function;

import com.google.common.base.Preconditions;

/**
 * When your DTO has fields of custom types you'll need to explicitly tell DAO layer what SQL types
 * must be used and how to convert values of those types to SQL-friendly values. Use this class to
 * describe such conversion.
 *
 * @author Sergey Karpushin
 */
public class SqlTypeOverride {
  protected int sqlType;
  protected boolean supportsSubclasses;
  protected Function<Object, Object> valueConverter;
  protected Class<Object> valueClass;

  /**
   * @param <TValueType> type of field that needs to be converted
   * @param <TSqlFriendlyType> SQL-friendlyName
   * @param valueClass class of the value
   * @param type one of {@link Types}
   * @param valueConverter function to convert DTO's field value to sql friendly value
   * @return new instance of SqlTypeOverride for given parameters
   */
  public static <TValueType, TSqlFriendlyType> SqlTypeOverride of(
      Class<TValueType> valueClass,
      int type,
      Function<TValueType, TSqlFriendlyType> valueConverter) {
    return of(valueClass, type, false, valueConverter);
  }

  /**
   * @param <TValueType> type of field that needs to be converted
   * @param <TSqlFriendlyType> SQL-friendlyName
   * @param valueClass class of the value
   * @param type one of {@link Types}
   * @param valueConverter function to convert DTO's field value to sql friendly value
   * @param supportsSubclasses if true then impl will also match subclasses of valueClass
   * @return new instance of SqlTypeOverride for given parameters
   */
  @SuppressWarnings("unchecked")
  public static <TValueType, TSqlFriendlyType> SqlTypeOverride of(
      Class<TValueType> valueClass,
      int type,
      boolean supportsSubclasses,
      Function<TValueType, TSqlFriendlyType> valueConverter) {
    Preconditions.checkNotNull(valueConverter, "valueConverter required");
    Preconditions.checkNotNull(valueClass, "valueClass required");

    SqlTypeOverride ret = new SqlTypeOverride();
    ret.sqlType = type;
    ret.valueConverter = (Function<Object, Object>) valueConverter;
    ret.supportsSubclasses = supportsSubclasses;
    ret.valueClass = (Class<Object>) valueClass;
    return ret;
  }

  public boolean supportsType(Class<?> valueClass) {
    if (!supportsSubclasses) {
      return this.valueClass.equals(valueClass);
    } else {
      return this.valueClass.isAssignableFrom(valueClass);
    }
  }

  public Object convert(Object value) {
    if (value == null) {
      return null;
    }
    return valueConverter.apply(value);
  }

  public int getSqlType() {
    return sqlType;
  }

  public Class<?> getValueClass() {
    return valueClass;
  }

  public boolean isSupportsSubclasses() {
    return supportsSubclasses;
  }
}
