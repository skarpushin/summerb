package org.summerb.easycrud.dao;

import java.sql.Types;

/**
 * A common impl for Enum-based field types
 *
 * @author Sergey Karpushin
 */
public class SqlTypeOverrideEnum extends SqlTypeOverride {

  @SuppressWarnings({"unchecked", "rawtypes"})
  public SqlTypeOverrideEnum() {
    this.sqlType = Types.VARCHAR;
    this.valueClass = (Class) Enum.class;
    this.supportsSubclasses = true;
    this.valueConverter = e -> ((Enum<?>) e).name();
  }
}
