package org.summerb.methodCapturers;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.summerb.validation.testDtos.Bean;

class PropertyNameResolverImplTest {

  MethodCapturerProxyClassFactory methodCapturerProxyClassFactory =
      new MethodCapturerProxyClassFactoryImpl();

  @Test
  void test_constructor() {
    assertThrows(IllegalArgumentException.class, () -> new PropertyNameResolverImpl<Bean>(null));
    assertThrows(
        IllegalArgumentException.class, () -> new PropertyNameResolverCachedImpl<Bean>(null));
    assertThrows(IllegalArgumentException.class, () -> new PropertyNameResolverFactoryImpl(null));
  }

  @Test
  void test_iae() {
    assertThrows(
        IllegalArgumentException.class,
        () -> PropertyNameResolverImpl.getPropertyNameFromGetterName(null));

    var f =
        new PropertyNameResolverImpl<Bean>(
            () -> methodCapturerProxyClassFactory.buildProxyFor(Bean.class));
    assertThrows(IllegalArgumentException.class, () -> f.resolve(null));
  }

  @Test
  void test_resolve() {
    MethodCapturer methodCapturer = methodCapturerProxyClassFactory.buildProxyFor(Bean.class);

    // simulate previous method acquisition
    Method method1 = Bean.class.getDeclaredMethods()[0];
    methodCapturer.set__Method(method1);
    assertEquals(method1, methodCapturer.get__Method());

    // now simulate failure - expect method name was not captured
    var f = new PropertyNameResolverImpl<Bean>(() -> methodCapturer);
    IllegalStateException ex =
        assertThrows(
            IllegalStateException.class,
            () ->
                f.resolve(
                    x -> {
                      throw new IllegalStateException("test");
                    }));
    assertEquals("Method was not captured", ex.getMessage());

    // now simulate usage of a non-getter method signature
    assertThrows(
        IllegalArgumentException.class,
        () ->
            f.resolve(
                x -> {
                  x.getWrongMethod(1);
                  return null;
                }));

    // now simulate usage of a method with non-getter name
    assertThrows(IllegalArgumentException.class, () -> f.resolve(Bean::toString));

    // valid - get
    assertEquals("iValue1", f.resolve(Bean::getiValue1));

    // valid - is
    assertEquals("bValue1", f.resolve(Bean::isbValue1));
  }
}
