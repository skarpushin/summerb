package org.summerb.easycrud.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.summerb.easycrud.api.RowCloner;
import org.summerb.utils.objectcopy.Clonnable;

/** A shallow-copy cloner which is MUCH faster than {@link RowClonerDeepCopyImpl} */
public class RowClonerReflectionImpl implements RowCloner {
  public static class MethodsPair {
    Method getter;
    Method setter;

    public MethodsPair(Method getter, Method setter) {
      this.getter = getter;
      this.setter = setter;
    }
  }

  protected LoadingCache<Class<?>, List<MethodsPair>> cloners;

  public RowClonerReflectionImpl() {
    this.cloners = CacheBuilder.newBuilder().build(buildLoader());
  }

  protected CacheLoader<? super Class<?>, List<MethodsPair>> buildLoader() {
    return new CacheLoader<>() {
      @Override
      public List<MethodsPair> load(Class<?> key) throws Exception {
        List<MethodsPair> ret = new LinkedList<>();
        BeanWrapper wrapper = new BeanWrapperImpl(key.getDeclaredConstructor().newInstance());
        for (PropertyDescriptor prop : wrapper.getPropertyDescriptors()) {
          Method readMethod = prop.getReadMethod();
          Method writeMethod = prop.getWriteMethod();
          if (readMethod != null && writeMethod != null) {
            ret.add(new MethodsPair(readMethod, writeMethod));
          }
        }
        return ret;
      }
    };
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object clone(Object row) {
    if (row instanceof Clonnable) {
      return ((Clonnable<Object>) row).clone();
    }

    if (row == null) {
      return null;
    }

    try {
      List<MethodsPair> methodsPairs = cloners.getUnchecked(row.getClass());
      Object ret = row.getClass().getDeclaredConstructor().newInstance();
      for (MethodsPair pair : methodsPairs) {
        pair.setter.invoke(ret, pair.getter.invoke(row));
      }
      return ret;
    } catch (Exception e) {
      throw new RuntimeException("Failed to clone DTO/ROW " + row.getClass(), e);
    }
  }
}
