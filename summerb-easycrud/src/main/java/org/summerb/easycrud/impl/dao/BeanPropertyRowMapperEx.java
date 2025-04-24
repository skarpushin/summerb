package org.summerb.easycrud.impl.dao;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * This is the exact copy of Spring jdbc BeanPropertyRowMapper with eliminated slight design flaws
 * which prevents this class to be adjusted as/if/when needed
 *
 * @param <T>
 */
public class BeanPropertyRowMapperEx<T> implements RowMapper<T> {

  /** Logger available to subclasses. */
  protected final Log logger = LogFactory.getLog(getClass());

  /** The class we are mapping to. */
  protected final Class<T> mappedClass;

  /** Whether we're strictly validating. */
  protected boolean checkFullyPopulated = false;

  /**
   * Whether {@code NULL} database values should be ignored for primitive properties in the target
   * class.
   *
   * @see #setPrimitivesDefaultedForNullValue(boolean)
   */
  protected boolean primitivesDefaultedForNullValue = false;

  /** ConversionService for binding JDBC values to bean properties. */
  @Nullable
  protected ConversionService conversionService = DefaultConversionService.getSharedInstance();

  /** Map of the properties we provide mapping for. */
  @Nullable protected Map<String, PropertyDescriptor> mappedProperties;

  /** Set of bean property names we provide mapping for. */
  @Nullable protected Set<String> mappedPropertyNames;

  /**
   * Create a new {@code BeanPropertyRowMapper}, accepting unpopulated properties in the target
   * bean.
   *
   * @param mappedClass the class that each row should be mapped to
   */
  public BeanPropertyRowMapperEx(Class<T> mappedClass) {
    this.mappedClass = mappedClass;
    initialize();
  }

  /**
   * Create a new {@code BeanPropertyRowMapper}.
   *
   * @param mappedClass the class that each row should be mapped to
   * @param checkFullyPopulated whether we're strictly validating that all bean properties have been
   *     mapped from corresponding database columns
   */
  public BeanPropertyRowMapperEx(Class<T> mappedClass, boolean checkFullyPopulated) {
    this.mappedClass = mappedClass;
    this.checkFullyPopulated = checkFullyPopulated;
    initialize();
  }

  /** Get the class that we are mapping to. */
  @Nullable
  public final Class<T> getMappedClass() {
    return this.mappedClass;
  }

  /**
   * Return whether we're strictly validating that all bean properties have been mapped from
   * corresponding database columns.
   */
  public boolean isCheckFullyPopulated() {
    return this.checkFullyPopulated;
  }

  /**
   * Set whether a {@code NULL} database column value should be ignored when mapping to a
   * corresponding primitive property in the target class.
   *
   * <p>Default is {@code false}, throwing an exception when nulls are mapped to Java primitives.
   *
   * <p>If this flag is set to {@code true} and you use an <em>ignored</em> primitive property value
   * from the mapped bean to update the database, the value in the database will be changed from
   * {@code NULL} to the current value of that primitive property. That value may be the property's
   * initial value (potentially Java's default value for the respective primitive type), or it may
   * be some other value set for the property in the default constructor (or initialization block)
   * or as a side effect of setting some other property in the mapped bean.
   */
  public void setPrimitivesDefaultedForNullValue(boolean primitivesDefaultedForNullValue) {
    this.primitivesDefaultedForNullValue = primitivesDefaultedForNullValue;
  }

  /**
   * Get the value of the {@code primitivesDefaultedForNullValue} flag.
   *
   * @see #setPrimitivesDefaultedForNullValue(boolean)
   */
  public boolean isPrimitivesDefaultedForNullValue() {
    return this.primitivesDefaultedForNullValue;
  }

  /**
   * Set a {@link ConversionService} for binding JDBC values to bean properties, or {@code null} for
   * none.
   *
   * <p>Default is a {@link DefaultConversionService}, as of Spring 4.3. This provides support for
   * {@code java.time} conversion and other special types.
   *
   * @since 4.3
   * @see #initBeanWrapper(BeanWrapper)
   */
  public void setConversionService(@Nullable ConversionService conversionService) {
    this.conversionService = conversionService;
  }

  /**
   * Return a {@link ConversionService} for binding JDBC values to bean properties, or {@code null}
   * if none.
   *
   * @since 4.3
   */
  @Nullable
  public ConversionService getConversionService() {
    return this.conversionService;
  }

  /** Initialize the mapping meta-data for the given class. */
  protected void initialize() {
    this.mappedProperties = new HashMap<>();
    this.mappedPropertyNames = new HashSet<>();

    for (PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(mappedClass)) {
      if (pd.getWriteMethod() != null) {
        initializeMappingFor(pd);
      }
    }
  }

  protected void initializeMappingFor(PropertyDescriptor pd) {
    String lowerCaseName = lowerCaseName(pd.getName());
    mappedProperties.put(lowerCaseName, pd);
    String underscoreName = underscoreName(pd.getName());
    if (!lowerCaseName.equals(underscoreName)) {
      mappedProperties.put(underscoreName, pd);
    }
    mappedPropertyNames.add(pd.getName());
  }

  /**
   * Remove the specified property from the mapped properties.
   *
   * @param propertyName the property name (as used by property descriptors)
   * @since 5.3.9
   */
  protected void suppressProperty(String propertyName) {
    if (this.mappedProperties != null) {
      this.mappedProperties.remove(lowerCaseName(propertyName));
      this.mappedProperties.remove(underscoreName(propertyName));
    }
  }

  /**
   * Convert the given name to lower case. By default, conversions will happen within the US locale.
   *
   * @param name the original name
   * @return the converted name
   * @since 4.2
   */
  protected String lowerCaseName(String name) {
    return name.toLowerCase(Locale.US);
  }

  /**
   * Convert a name in camelCase to an underscored name in lower case. Any upper case letters are
   * converted to lower case with a preceding underscore.
   *
   * @param name the original name
   * @return the converted name
   * @since 4.2
   * @see #lowerCaseName
   */
  protected String underscoreName(String name) {
    if (!StringUtils.hasLength(name)) {
      return "";
    }

    StringBuilder result = new StringBuilder();
    result.append(Character.toLowerCase(name.charAt(0)));
    for (int i = 1; i < name.length(); i++) {
      char c = name.charAt(i);
      if (Character.isUpperCase(c)) {
        result.append('_').append(Character.toLowerCase(c));
      } else {
        result.append(c);
      }
    }
    return result.toString();
  }

  /**
   * Extract the values for all columns in the current row.
   *
   * <p>Utilizes public setters and result set meta-data.
   *
   * @see java.sql.ResultSetMetaData
   */
  @Override
  public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
    BeanWrapperImpl bw = new BeanWrapperImpl();
    initBeanWrapper(bw);

    T mappedObject = constructMappedInstance(rs, bw);
    bw.setBeanInstance(mappedObject);

    ResultSetMetaData rsmd = rs.getMetaData();
    int columnCount = rsmd.getColumnCount();
    Set<String> populatedProperties = (isCheckFullyPopulated() ? new HashSet<>() : null);

    for (int index = 1; index <= columnCount; index++) {
      String column = JdbcUtils.lookupColumnName(rsmd, index);
      mapColumn(rs, rowNumber, column, index, bw, mappedObject, populatedProperties);
    }

    if (populatedProperties != null && !populatedProperties.equals(this.mappedPropertyNames)) {
      throw new InvalidDataAccessApiUsageException(
          "Given ResultSet does not contain all properties "
              + "necessary to populate object of "
              + this.mappedClass
              + ": "
              + this.mappedPropertyNames);
    }

    return mappedObject;
  }

  protected void mapColumn(
      ResultSet rs,
      int rowNumber,
      String column,
      int index,
      BeanWrapperImpl bw,
      T mappedObject,
      Set<String> populatedProperties)
      throws SQLException {
    String property = lowerCaseName(StringUtils.delete(column, " "));
    PropertyDescriptor pd =
        (this.mappedProperties != null ? this.mappedProperties.get(property) : null);
    if (pd == null) {
      return;
    }

    try {
      Object value = getColumnValue(rs, index, pd);
      if (rowNumber == 0 && logger.isDebugEnabled()) {
        logger.debug(
            "Mapping column '"
                + column
                + "' to property '"
                + pd.getName()
                + "' of type '"
                + ClassUtils.getQualifiedName(pd.getPropertyType())
                + "'");
      }
      try {
        bw.setPropertyValue(pd.getName(), value);
      } catch (TypeMismatchException ex) {
        if (value == null && this.primitivesDefaultedForNullValue) {
          if (logger.isDebugEnabled()) {
            String propertyType = ClassUtils.getQualifiedName(pd.getPropertyType());
            logger.debug(
                ("Ignoring intercepted TypeMismatchException for row %d and column '%s'\n"
                        + "with null value when setting property '%s' of type '%s' on object: %s\"\n")
                    .formatted(rowNumber, column, pd.getName(), propertyType, mappedObject),
                ex);
          }
        } else {
          throw ex;
        }
      }
      if (populatedProperties != null) {
        populatedProperties.add(pd.getName());
      }
    } catch (NotWritablePropertyException ex) {
      throw new DataRetrievalFailureException(
          "Unable to map column '" + column + "' to property '" + pd.getName() + "'", ex);
    }
  }

  /**
   * Construct an instance of the mapped class for the current row.
   *
   * @param rs the ResultSet to map (pre-initialized for the current row)
   * @param tc a TypeConverter with this RowMapper's conversion service
   * @return a corresponding instance of the mapped class
   * @throws SQLException if an SQLException is encountered
   * @since 5.3
   */
  protected T constructMappedInstance(ResultSet rs, TypeConverter tc) throws SQLException {
    Assert.state(this.mappedClass != null, "Mapped class was not specified");
    return BeanUtils.instantiateClass(this.mappedClass);
  }

  /**
   * Initialize the given BeanWrapper to be used for row mapping. To be called for each row.
   *
   * <p>The default implementation applies the configured {@link ConversionService}, if any. Can be
   * overridden in subclasses.
   *
   * @param bw the BeanWrapper to initialize
   * @see #getConversionService()
   * @see BeanWrapper#setConversionService
   */
  protected void initBeanWrapper(BeanWrapper bw) {
    ConversionService cs = getConversionService();
    if (cs != null) {
      bw.setConversionService(cs);
    }
  }

  /**
   * Retrieve a JDBC object value for the specified column.
   *
   * <p>The default implementation delegates to {@link #getColumnValue(ResultSet, int, Class)}.
   *
   * @param rs is the ResultSet holding the data
   * @param index is the column index
   * @param pd the bean property that each result object is expected to match
   * @return the Object value
   * @throws SQLException in case of extraction failure
   * @see #getColumnValue(ResultSet, int, Class)
   */
  @Nullable
  protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd)
      throws SQLException {
    return JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
  }

  /**
   * Retrieve a JDBC object value for the specified column.
   *
   * <p>The default implementation calls {@link JdbcUtils#getResultSetValue(java.sql.ResultSet, int,
   * Class)}. Subclasses may override this to check specific value types upfront, or to post-process
   * values return from {@code getResultSetValue}.
   *
   * @param rs is the ResultSet holding the data
   * @param index is the column index
   * @param paramType the target parameter type
   * @return the Object value
   * @throws SQLException in case of extraction failure
   * @since 5.3
   * @see org.springframework.jdbc.support.JdbcUtils#getResultSetValue(java.sql.ResultSet, int,
   *     Class)
   */
  @Nullable
  protected Object getColumnValue(ResultSet rs, int index, Class<?> paramType) throws SQLException {
    return JdbcUtils.getResultSetValue(rs, index, paramType);
  }
}
