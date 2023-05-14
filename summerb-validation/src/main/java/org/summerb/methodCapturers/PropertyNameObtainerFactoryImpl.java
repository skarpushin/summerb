package org.summerb.methodCapturers;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class PropertyNameObtainerFactoryImpl implements PropertyNameObtainerFactory {
  protected MethodCapturerProxyClassFactory methodCapturerProxyClassFactory;
  protected LoadingCache<Class<?>, PropertyNameObtainer<?>> propertyNameObtainers;

  public PropertyNameObtainerFactoryImpl(
      @Nonnull MethodCapturerProxyClassFactory methodCapturerProxyClassFactory) {
    Preconditions.checkArgument(
        methodCapturerProxyClassFactory != null, "methodCapturerProxyClassFactory required");
    this.methodCapturerProxyClassFactory = methodCapturerProxyClassFactory;

    propertyNameObtainers = CacheBuilder.newBuilder().build(loader);
  }

  protected CacheLoader<Class<?>, PropertyNameObtainer<?>> loader =
      new CacheLoader<Class<?>, PropertyNameObtainer<?>>() {
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public PropertyNameObtainer<?> load(Class<?> key) throws Exception {
          return new PropertyNameObtainerCachedImpl(
              new PropertyNameObtainerImpl(
                  () -> methodCapturerProxyClassFactory.buildProxyFor(key)));
        }
      };

  @SuppressWarnings("unchecked")
  @Override
  public <T> PropertyNameObtainer<T> getObtainer(Class<T> beanClass) {
    return (PropertyNameObtainer<T>) propertyNameObtainers.getUnchecked(beanClass);
  }
}
