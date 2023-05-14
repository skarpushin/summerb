package org.summerb.methodCapturers;

import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Cached impl of {@link PropertyNameObtainer}
 *
 * <p>Lambda that is created is tied to a place where it was created, so when same code is executed
 * again and again it will reuse same lAmbda, which means we can use it to cache results.
 *
 * @param <T> type of bean for which we can retrieve propertyNames from method references
 */
public class PropertyNameObtainerCachedImpl<T> implements PropertyNameObtainer<T> {
  protected final PropertyNameObtainer<T> actual;

  protected LoadingCache<Function<?, ?>, String> cache;

  public PropertyNameObtainerCachedImpl(PropertyNameObtainer<T> actual) {
    Preconditions.checkArgument(actual != null, "actual requried");
    this.actual = actual;
    cache = CacheBuilder.newBuilder().build(loader);
  }

  protected CacheLoader<Function<?, ?>, String> loader =
      new CacheLoader<Function<?, ?>, String>() {
        @SuppressWarnings("unchecked")
        @Override
        public String load(Function<?, ?> key) throws Exception {
          return actual.obtainFrom((Function<T, ?>) key);
        }
      };

  @Override
  public String obtainFrom(Function<T, ?> methodReference) {
    return cache.getUnchecked(methodReference);
  }
}
