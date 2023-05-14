package org.summerb.easycrud.gen2;

import org.summerb.methodCapturers.MethodCapturerProxyClassFactoryImpl;
import org.summerb.methodCapturers.PropertyNameObtainerFactoryImpl;

public interface QueryExFactory {

  /**
   * This factory will be used to build instances of {@link QueryEx}. This is an indirection that
   * you can use to customize/override default behavior, primarily in cases when you'll want to
   * extend functionality
   *
   * <p>TODO: Encourage to set bean from application context
   */
  static QueryExFactory FACTORY =
      new QueryExFactoryImpl(
          new PropertyNameObtainerFactoryImpl(new MethodCapturerProxyClassFactoryImpl()));

  /**
   * @param <T> type of Row (POJO)
   * @param clazz Row (POJO) class which getters will be used to extract field names
   * @return instance that can be used for both - referring to fields using method references as
   *     well as string literals
   */
  <T, F extends QueryEx<T>> F buildFor(Class<T> clazz);

  /**
   * @return instance that can be used only to refer to fields using string literals. Not
   *     recommended as in such case you'll use string literals and loose all power of IDE and
   *     Compiler static code analysis
   */
  QueryEx<?> build();
}
