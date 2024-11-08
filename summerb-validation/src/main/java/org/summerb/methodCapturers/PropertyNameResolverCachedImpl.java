/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.methodCapturers;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.function.Function;

/**
 * Cached impl of {@link PropertyNameResolver}
 *
 * <p>Lambda that is created is tied to a place where it was created, so when same code is executed
 * again and again it will reuse same lambda, which means we can use it to cache results.
 *
 * @param <T> type of bean for which we can retrieve propertyNames from method references
 */
public class PropertyNameResolverCachedImpl<T> implements PropertyNameResolver<T> {
  protected final PropertyNameResolver<T> actual;

  protected LoadingCache<Function<?, ?>, String> cache;

  public PropertyNameResolverCachedImpl(PropertyNameResolver<T> actual) {
    Preconditions.checkArgument(actual != null, "actual required");
    this.actual = actual;
    cache = CacheBuilder.newBuilder().build(loader);
  }

  protected CacheLoader<Function<?, ?>, String> loader =
      new CacheLoader<>() {
        @SuppressWarnings("unchecked")
        @Override
        public String load(Function<?, ?> key) {
          return actual.resolve((Function<T, ?>) key);
        }
      };

  @Override
  public String resolve(Function<T, ?> methodReference) {
    return cache.getUnchecked(methodReference);
  }
}
