package org.summerb.easycrud.dao;

public interface SqlTypeOverrides {

  SqlTypeOverride findOverrideForValue(Object value);

  SqlTypeOverride findOverrideForClass(Class<?> valueClass);
}
