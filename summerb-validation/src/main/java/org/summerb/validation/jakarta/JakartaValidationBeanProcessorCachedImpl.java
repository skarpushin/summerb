package org.summerb.validation.jakarta;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class JakartaValidationBeanProcessorCachedImpl implements JakartaValidationBeanProcessor {
  protected JakartaValidationBeanProcessor actual;

  protected LoadingCache<Class<?>, List<JakartaValidatorItem>> cache;

  public JakartaValidationBeanProcessorCachedImpl(@Nonnull JakartaValidationBeanProcessor actual) {
    Preconditions.checkArgument(actual != null, "actual required");
    this.actual = actual;

    cache = CacheBuilder.newBuilder().build(loader);
  }

  protected CacheLoader<Class<?>, List<JakartaValidatorItem>> loader =
      new CacheLoader<>() {
        @Override
        public List<JakartaValidatorItem> load(Class<?> clazz) {
          return actual.getValidationsFor(clazz);
        }
      };

  @Override
  public @Nonnull List<JakartaValidatorItem> getValidationsFor(Class<?> clazz) {
    try {
      return cache.get(clazz);
    } catch (Exception e) {
      if (e.getCause() instanceof RuntimeException) {
        throw (RuntimeException) e.getCause();
      }
      // NOTE: Cannot test this branch -- JakartaValidationBeanProcessor#getValidationsFor is not
      // throwing checked exceptions. But still I want to keep this here just in case.
      throw new RuntimeException("Failed to get validators for " + clazz, e);
    }
  }
}
