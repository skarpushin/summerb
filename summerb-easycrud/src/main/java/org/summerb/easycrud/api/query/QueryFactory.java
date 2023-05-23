package org.summerb.easycrud.api.query;

public interface QueryFactory {

  /**
   * @param <T> type of Row (POJO)
   * @param clazz Row (POJO) class which getters will be used to extract field names
   * @return instance that can be used for both - referring to fields using method references as
   *     well as string literals
   */
  <T, F extends Query<T>> F buildFor(Class<T> clazz);

  /**
   * @return instance that can be used only to refer to fields using string literals. Not
   *     recommended as in such case you'll use string literals and loose all power of IDE and
   *     Compiler static code analysis
   */
  Query<?> build();
}
