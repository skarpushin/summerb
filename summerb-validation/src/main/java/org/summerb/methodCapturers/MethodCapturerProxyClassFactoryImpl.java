package org.summerb.methodCapturers;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

public class MethodCapturerProxyClassFactoryImpl implements MethodCapturerProxyClassFactory {

  protected Object syncRoot = new Object();
  protected volatile Map<Class<?>, Class<MethodCapturer>> beanClassToProxyType =
      Collections.unmodifiableMap(new HashMap<>());

  @SuppressWarnings("deprecation")
  @Override
  public @Nonnull MethodCapturer buildProxyFor(@Nonnull Class<?> clazz) {
    try {
      return (MethodCapturer) getProxyType(clazz).newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Failed to instantiate MethodCapturer for class " + clazz, e);
    }
  }

  protected Class<MethodCapturer> getProxyType(Class<?> clazz) {
    Preconditions.checkArgument(clazz != null, "clazz required");

    Class<MethodCapturer> ret = findProxyInCacheForClass(clazz);
    if (ret != null) {
      return ret;
    }

    synchronized (syncRoot) {
      ret = findProxyInCacheForClass(clazz);
      if (ret != null) {
        // NOTE: I don't know why pitest thinks that "return null" mutation can't be killed -- I've
        // traced, and this path is well tested with "test_getProxyType_multi_threaded_double_check"
        // test
        return ret;
      }

      ret = assembleProxyClass(clazz);

      // NOTE: This "switch" is done to avoid edge case of modifying map that might be used by other
      // thread for reading.
      Map<Class<?>, Class<MethodCapturer>> newMap = new HashMap<>(beanClassToProxyType);
      newMap.put(clazz, ret);
      beanClassToProxyType = Collections.unmodifiableMap(newMap);

      return ret;
    }
  }

  /**
   * Find cached proxy class.
   *
   * @param clazz a class for which we need to returned cached Proxy class
   * @return Proxy class if cached, or null
   */
  @VisibleForTesting
  protected Class<MethodCapturer> findProxyInCacheForClass(Class<?> clazz) {
    return beanClassToProxyType.get(clazz);
  }

  @SuppressWarnings("unchecked")
  protected Class<MethodCapturer> assembleProxyClass(Class<?> clazz) {
    return (Class<MethodCapturer>)
        new ByteBuddy()
            .subclass(clazz)
            .implement(MethodCapturer.class)
            .defineField("__Method", Method.class, Visibility.PRIVATE)
            .method(ElementMatchers.any())
            .intercept(MethodDelegation.to(MethodCapturingInterceptor.class))
            .method(ElementMatchers.named("set__Method").or(ElementMatchers.named("get__Method")))
            .intercept(FieldAccessor.ofBeanProperty())
            .make()
            .load(MethodCapturer.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
            .getLoaded();
  }
}
