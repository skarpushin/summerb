package org.summerb.easycrud.sql_builder;

import java.util.List;

public interface FieldsEnlister {
  List<String> findInClass(Class<?> rowClass);
}
