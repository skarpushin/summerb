package org.summerb.easycrud.sql_builder.impl;

import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.summerb.easycrud.sql_builder.FieldsEnlister;

/**
 * Utility class for creating comma-separated list of fields from POJO-styled Row classes with
 * prefix and aliases. This allows for more concise SQL queries when selecting fields from multiple
 * tables.
 */
public class FieldsEnlisterImpl implements FieldsEnlister {
  public FieldsEnlisterImpl() {}

  @Override
  public List<String> findInClass(Class<?> rowClass) {
    Preconditions.checkNotNull(rowClass, "rowClass is required");

    List<String> fields = new ArrayList<>();

    // Process fields from the class itself
    enlistFields(rowClass, fields);

    // Process fields from parent classes (except Object class)
    Class<?> superClass = rowClass.getSuperclass();
    while (superClass != null && !superClass.equals(Object.class)) {
      enlistFields(superClass, fields);
      superClass = superClass.getSuperclass();
    }

    return fields;
  }

  protected void enlistFields(Class<?> clazz, List<String> out) {
    for (Field field : clazz.getDeclaredFields()) {
      if (field.isSynthetic()) {
        continue;
      }
      if (Modifier.isStatic(field.getModifiers())) {
        continue;
      }
      out.add(field.getName());
    }
  }
}
