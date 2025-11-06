package org.summerb.easycrud.sql_builder.impl;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.List;
import org.summerb.easycrud.sql_builder.FieldsEnlister;

public class FieldsEnlisterCachingImpl implements FieldsEnlister {
  protected FieldsEnlister delegate;

  protected LoadingCache<Class<?>, List<String>> cacheFieldsList;

  public FieldsEnlisterCachingImpl(FieldsEnlister delegate) {
    Preconditions.checkNotNull(delegate, "delegate is required");
    this.delegate = delegate;

    cacheFieldsList = CacheBuilder.newBuilder().build(cacheFieldsListLoader);
  }

  protected CacheLoader<Class<?>, List<String>> cacheFieldsListLoader =
      new CacheLoader<>() {
        @Override
        public List<String> load(Class<?> key) {
          return delegate.findInClass(key);
        }
      };

  @Override
  public List<String> findInClass(Class<?> rowClass) {
    return cacheFieldsList.getUnchecked(rowClass);
  }
}
