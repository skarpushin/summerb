package org.summerb.easycrud.join_query;

import org.summerb.easycrud.join_query.model.ReferringTo;

/** Helps to find relevant fields marked with {@link ReferringTo} annotation */
public interface ReferringToFieldsFinder {
  /**
   * Traverse all fields of fromRow (including superclasses) and attempt to find field that has
   * annotation ReferringTo that is both: 1) has Class matching toRow as a value; and 2) field type
   * matches type of ID of toRow (primitive and boxed types are deemed the same, i.e. long and
   * Long).
   *
   * <p>If field with matching annotation is found, but something else is wrong (wrong id type or
   * more than 1 matching field) WARN log message will be logged and null result will be returned
   *
   * @param fromRow row which has a reference
   * @param toRow row which is referenced
   * @return found field name, or null if field not found, id type doesn't match or there is more
   *     that matching field
   */
  String findReferringField(Class<?> fromRow, Class<?> toRow);
}
