package org.summerb.easycrud.join_query.impl;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * Implementation of {@link RowMapper} that maps a row to a new instance of the specified mapped
 * target class. The mapped target class must be a top-level class and it must have a default or
 * no-arg constructor.
 *
 * <p>Column values are mapped based on matching the column name (as obtained from the ResultSet
 * metadata) to the field name with a prefix in the target class.
 *
 * <p>This implementation is designed to handle prefixed fields in the ResultSet, which is useful
 * when joining multiple tables with overlapping column names.
 *
 * @param <T> the result type
 */
public class PrefixedFieldsRowMapper<T> implements RowMapper<T> {

  protected final Class<T> mappedClass;
  protected final String prefix;
  protected final Map<String, PropertyDescriptor> mappedFields;
  protected final Set<String> mappedProperties;

  /**
   * Create a new PrefixedFieldsRowMapper for the given target class.
   *
   * @param mappedClass the class that each row should be mapped to
   * @param prefix the prefix to be removed from column names when mapping to fields
   */
  public PrefixedFieldsRowMapper(Class<T> mappedClass, String prefix) {
    this.mappedClass = mappedClass;
    this.prefix = prefix;
    this.mappedFields = new HashMap<>();
    this.mappedProperties = new HashSet<>();

    PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);
    for (PropertyDescriptor pd : pds) {
      if (pd.getWriteMethod() != null) {
        String lowerCasePdName = pd.getName().toLowerCase();
        this.mappedFields.put(lowerCasePdName, pd);
        this.mappedProperties.add(pd.getName());
      }
    }
  }

  @Override
  public T mapRow(ResultSet rs, int rowNum) throws SQLException {
    T mappedObject;
    try {
      mappedObject = BeanUtils.instantiateClass(this.mappedClass);
    } catch (Exception ex) {
      throw new DataRetrievalFailureException(
          "Unable to instantiate target class: " + this.mappedClass.getName(), ex);
    }

    BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);

    ResultSetMetaData rsmd = rs.getMetaData();
    int columnCount = rsmd.getColumnCount();

    for (int index = 1; index <= columnCount; index++) {
      String column = JdbcUtils.lookupColumnName(rsmd, index);
      String field = column.toLowerCase();

      // Skip columns that don't start with the prefix
      if (!field.startsWith(this.prefix.toLowerCase())) {
        continue;
      }

      // Remove the prefix from the field name
      field = field.substring(this.prefix.length());

      PropertyDescriptor pd = this.mappedFields.get(field);
      if (pd != null) {
        try {
          Object value = JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
          if (value != null) {
            bw.setPropertyValue(pd.getName(), value);
          }
        } catch (TypeMismatchException e) {
          // Handle type conversion errors
          throw new DataRetrievalFailureException(
              "Unable to map column '" + column + "' to property '" + pd.getName() + "'", e);
        }
      }
    }

    return mappedObject;
  }
}
