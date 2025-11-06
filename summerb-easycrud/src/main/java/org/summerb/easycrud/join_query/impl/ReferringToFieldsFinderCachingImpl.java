package org.summerb.easycrud.join_query.impl;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.UUID;
import org.summerb.easycrud.join_query.ReferringToFieldsFinder;

public class ReferringToFieldsFinderCachingImpl implements ReferringToFieldsFinder {
  protected ReferringToFieldsFinder actual;

  protected LoadingCache<Key, String> cache;
  protected static final String NO_RESULT = UUID.randomUUID().toString();

  public ReferringToFieldsFinderCachingImpl(ReferringToFieldsFinder actual) {
    Preconditions.checkNotNull(actual, "actual is required");
    this.actual = actual;

    cache = CacheBuilder.newBuilder().build(loader);
  }

  protected final CacheLoader<Key, String> loader =
      new CacheLoader<>() {
        @Override
        public String load(Key key) {
          String ret = actual.findReferringField(key.fromRow, key.toRow);
          if (ret == null) {
            return NO_RESULT;
          }
          return ret;
        }
      };

  @Override
  public String findReferringField(Class<?> fromRow, Class<?> toRow) {
    String ret = cache.getUnchecked(new Key(fromRow, toRow));
    if (NO_RESULT.equals(ret)) {
      return null;
    }
    return ret;
  }

  protected record Key(Class<?> fromRow, Class<?> toRow) {}
}
