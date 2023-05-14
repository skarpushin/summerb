package org.summerb.easycrud.gen2._trash;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

public class FieldNameCapturingProxy<T> implements InvocationHandler {
  private Class<T> rowClass;
  private String capturedFieldName;
  public T proxy;

  public FieldNameCapturingProxy(Class<T> rowClass) {
    this.rowClass = rowClass;
  }

  public void clear() {
    capturedFieldName = null;
  }

  public String getCapturedFieldName() {
    return capturedFieldName;
  }

  public static <T> FieldNameCapturingProxy<T> buildFor(Class<T> rowClass) {
    FieldNameCapturingProxy<T> ret = new FieldNameCapturingProxy<>(rowClass);
    try {
      ret.proxy =
          (T)
              new ByteBuddy()
                  .subclass(rowClass)
                  .method(ElementMatchers.any())
                  .intercept(InvocationHandlerAdapter.of(ret))
                  .make()
                  .load(rowClass.getClassLoader())
                  .getLoaded()
                  .newInstance();
    } catch (Exception e) {
      throw new RuntimeException("failed to create proxy", e);
    }
    return ret;
  }

  @Override
  public Object invoke(Object instance, Method method, Object[] args) throws Throwable {
    capturedFieldName = method.getName();
    return null;
  }
}
