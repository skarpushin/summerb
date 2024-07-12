/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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

public class PropertyNameResolverFactoryImpl implements PropertyNameResolverFactory {
  protected MethodCapturerProxyClassFactory methodCapturerProxyClassFactory;
  protected LoadingCache<Class<?>, PropertyNameResolver<?>> propertyNameObtainers;
  protected CacheLoader<Class<?>, PropertyNameResolver<?>> loader;

  public PropertyNameResolverFactoryImpl(
      MethodCapturerProxyClassFactory methodCapturerProxyClassFactory) {
    Preconditions.checkArgument(
        methodCapturerProxyClassFactory != null, "methodCapturerProxyClassFactory required");
    this.methodCapturerProxyClassFactory = methodCapturerProxyClassFactory;

    loader = buildLoader(methodCapturerProxyClassFactory);
    propertyNameObtainers = CacheBuilder.newBuilder().build(loader);
  }

  protected CacheLoader<Class<?>, PropertyNameResolver<?>> buildLoader(
      MethodCapturerProxyClassFactory methodCapturerProxyClassFactory) {
    return new CacheLoader<>() {
      @SuppressWarnings({"unchecked", "rawtypes"})
      @Override
      public PropertyNameResolver<?> load(Class<?> rowClass) {
        return new PropertyNameResolverCachedImpl(
            new PropertyNameResolverImpl(() -> methodCapturerProxyClassFactory.buildProxyFor(rowClass)));
      }
    };
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> PropertyNameResolver<T> getResolver(Class<T> beanClass) {
    return (PropertyNameResolver<T>) propertyNameObtainers.getUnchecked(beanClass);
  }
}
