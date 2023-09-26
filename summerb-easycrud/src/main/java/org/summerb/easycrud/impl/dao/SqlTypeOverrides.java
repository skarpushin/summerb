package org.summerb.easycrud.impl.dao;

public interface SqlTypeOverrides {

  SqlTypeOverride findOverrideForValue(Object value);

  SqlTypeOverride findOverrideForClass(Class<?> valueClass);
}
