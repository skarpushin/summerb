package org.summerb.easycrud.impl.dao;

import com.google.common.base.Preconditions;

public class SqlTypeOverridesDefaultImpl implements SqlTypeOverrides {
  protected SqlTypeOverride sqlTypeOverrideEnum;

  public SqlTypeOverridesDefaultImpl() {
    this.sqlTypeOverrideEnum = new SqlTypeOverrideEnum();
  }

  public SqlTypeOverridesDefaultImpl(SqlTypeOverride sqlTypeOverrideEnum) {
    Preconditions.checkNotNull(sqlTypeOverrideEnum, "sqlTypeOverrideEnum required");
    this.sqlTypeOverrideEnum = sqlTypeOverrideEnum;
  }

  @Override
  public SqlTypeOverride findOverrideForValue(Object value) {
    if (value == null) {
      return null;
    }
    return findOverrideForClass(value.getClass());
  }

  @Override
  public SqlTypeOverride findOverrideForClass(Class<?> valueClass) {
    if (valueClass == null) {
      return null;
    }

    if (sqlTypeOverrideEnum.supportsType(valueClass)) {
      return sqlTypeOverrideEnum;
    }

    return null;
  }
}
