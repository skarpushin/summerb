package org.summerb.easycrud.join_query.impl;

import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.summerb.easycrud.join_query.ReferringToFieldsFinder;
import org.summerb.easycrud.join_query.model.ReferringTo;
import org.summerb.easycrud.row.HasId;

public class ReferringToFieldsFinderImpl implements ReferringToFieldsFinder {
  private final Logger log = LoggerFactory.getLogger(getClass());

  public ReferringToFieldsFinderImpl() {}

  @Override
  public String findReferringField(Class<?> fromRow, Class<?> toRow) {
    Preconditions.checkArgument(fromRow != null, "fromRow required");
    Preconditions.checkArgument(toRow != null, "toRow required");

    // Get the ID type of toRow
    Class<?> toRowIdType = getRowIdType(toRow);

    // Find all fields in fromRow (including superclasses) that have @ReferringTo annotation
    List<Field> candidateFields = findMatchingAnnotatedFields(fromRow, toRow);

    // Check if we found exactly one matching field with correct type
    if (candidateFields.isEmpty()) {
      return null;
    }

    List<Field> validFields = findFieldsOfValidType(candidateFields, toRowIdType);
    if (validFields.size() == 1) {
      return validFields.get(0).getName();
    }

    // Log warnings for various error cases
    if (validFields.isEmpty()) {
      log.warn(
          "Found {} field(s) with @ReferringTo({}) in {}, but none have compatible ID type (expected: {})",
          candidateFields.size(),
          toRow.getSimpleName(),
          fromRow.getSimpleName(),
          toRowIdType.getSimpleName());
    } else {
      List<String> fieldNames = validFields.stream().map(Field::getName).toList();
      log.warn(
          "Found {} (more than 1) fields with @ReferringTo({}) and compatible ID type in {}: {}",
          validFields.size(),
          toRow.getSimpleName(),
          fromRow.getSimpleName(),
          fieldNames);
    }

    return null;
  }

  protected List<Field> findFieldsOfValidType(List<Field> candidateFields, Class<?> toRowIdType) {
    List<Field> validFields = new ArrayList<>();
    for (Field field : candidateFields) {
      if (isTypeCompatible(field.getType(), toRowIdType)) {
        validFields.add(field);
      }
    }
    return validFields;
  }

  protected static List<Field> findMatchingAnnotatedFields(Class<?> fromRow, Class<?> toRow) {
    List<Field> candidateFields = new ArrayList<>();
    Class<?> currentClass = fromRow;

    while (currentClass != null && currentClass != Object.class) {
      for (Field field : currentClass.getDeclaredFields()) {
        ReferringTo annotation = field.getAnnotation(ReferringTo.class);
        if (annotation != null && annotation.value().equals(toRow)) {
          candidateFields.add(field);
        }
      }
      currentClass = currentClass.getSuperclass();
    }
    return candidateFields;
  }

  /** Extracts the ID type from a class that implements HasId */
  protected Class<?> getRowIdType(Class<?> rowClass) {
    // Check if the class implements HasId
    if (!HasId.class.isAssignableFrom(rowClass)) {
      throw new IllegalArgumentException(
          ">" + rowClass.getSimpleName() + " does not implement HasId");
    }

    try {
      // Get the getId method to determine the return type (ID type)
      java.lang.reflect.Method getIdMethod = rowClass.getMethod("getId");
      return getIdMethod.getReturnType();
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(
          ">" + rowClass.getSimpleName() + " does not contain getId() method");
    }
  }

  /** Checks if two types are compatible, considering primitive/boxed type equivalence */
  protected boolean isTypeCompatible(Class<?> fieldType, Class<?> idType) {
    // Direct match
    if (fieldType.equals(idType)) {
      return true;
    }

    // Check for primitive/boxed type pairs
    if (fieldType.isPrimitive()) {
      return isSameType(fieldType, idType);
    }

    if (idType.isPrimitive()) {
      return isSameType(idType, fieldType);
    }

    return false;
  }

  protected static boolean isSameType(Class<?> primitiveType, Class<?> boxedType) {
    return (primitiveType == long.class && boxedType == Long.class)
        || (primitiveType == int.class && boxedType == Integer.class)
        || (primitiveType == byte.class && boxedType == Byte.class)
        || (primitiveType == char.class && boxedType == Character.class)
        || (primitiveType == short.class && boxedType == Short.class);
  }
}
