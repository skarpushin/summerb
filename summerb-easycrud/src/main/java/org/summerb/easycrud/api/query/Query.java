package org.summerb.easycrud.api.query;

import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.impl.query.QueryExFactoryImpl;
import org.summerb.methodCapturers.MethodCapturerProxyClassFactoryImpl;
import org.summerb.methodCapturers.PropertyNameResolver;
import org.summerb.methodCapturers.PropertyNameResolverFactoryImpl;

/**
 * A lightweight and simple way for building queries for {@link EasyCrudService}. It provides usual
 * conditions, nothing fancy (no aggregation, etc). If you need to build complex queries please
 * consider other options. But usually Query will provide sufficient facilities for querying rows.
 *
 * <p>It provides you with ability to specify field names two ways: a) Method references (it uses
 * ByteBuddy under the hood to extract field names) and b) using string literals.
 *
 * <p>It is not recommended to specify field names as string literals because then you loose all
 * power of static code analysis, compiler defense against typos and IDE features like call
 * hierarchy analysis and renaming
 *
 * @author Sergey Karpushin
 * @param <T> type of Row for which this query is being built
 */
public class Query<T> extends QueryShortcuts<T, Query<T>> {

  /**
   * This factory will be used to build instances of {@link Query}. This is an indirection that you
   * can use to customize/override default behavior, primarily in cases when you'll want to extend
   * functionality
   *
   * <p>IMPORTANT: It would be best if you initialize this field with Spring Bean during
   * configuration initialization
   */
  public static QueryFactory FACTORY =
      new QueryExFactoryImpl(
          new PropertyNameResolverFactoryImpl(new MethodCapturerProxyClassFactoryImpl()));

  protected final PropertyNameResolver<T> propertyNameResolver;

  public Query() {
    super();
    this.propertyNameResolver = null;
  }

  public Query(PropertyNameResolver<T> propertyNameResolver) {
    super(propertyNameResolver);
    this.propertyNameResolver = propertyNameResolver;
  }

  public static Query<?> n() {
    return FACTORY.build();
  }

  public static <T> Query<T> n(Class<T> clazz) {
    return FACTORY.buildFor(clazz);
  }
}
