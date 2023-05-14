package org.summerb.methodCapturers;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.summerb.validation.testDtos.Bean;

class PropertyNameObtainerImplTest {

  MethodCapturerProxyClassFactory methodCapturerProxyClassFactory =
      new MethodCapturerProxyClassFactoryImpl();

  @Test
  void test_constructor() {
    assertThrows(IllegalArgumentException.class, () -> new PropertyNameObtainerImpl<Bean>(null));
    assertThrows(
        IllegalArgumentException.class, () -> new PropertyNameObtainerCachedImpl<Bean>(null));
    assertThrows(IllegalArgumentException.class, () -> new PropertyNameObtainerFactoryImpl(null));
  }

  @Test
  void test_iae() {
    assertThrows(
        IllegalArgumentException.class,
        () -> PropertyNameObtainerImpl.getPropertyNameFromGetterName(null));

    var f =
        new PropertyNameObtainerImpl<Bean>(
            () -> methodCapturerProxyClassFactory.buildProxyFor(Bean.class));
    assertThrows(IllegalArgumentException.class, () -> f.obtainFrom(null));
  }

  @Test
  void test_obtainFrom() {
    MethodCapturer methodCapturer = methodCapturerProxyClassFactory.buildProxyFor(Bean.class);

    // simulate previous method acquisition
    Method method1 = Bean.class.getDeclaredMethods()[0];
    methodCapturer.set__Method(method1);
    assertEquals(method1, methodCapturer.get__Method());

    // now simulate failure - expect method name was not captured
    var f = new PropertyNameObtainerImpl<Bean>(() -> methodCapturer);
    IllegalStateException ex =
        assertThrows(
            IllegalStateException.class,
            () ->
                f.obtainFrom(
                    x -> {
                      throw new IllegalStateException("test");
                    }));
    assertEquals("Method was not captured", ex.getMessage());

    // now simulate usage of a non-getter method signature
    assertThrows(
        IllegalArgumentException.class,
        () ->
            f.obtainFrom(
                x -> {
                  x.getWrongMethod(1);
                  return null;
                }));

    // now simulate usage of a method with non-getter name
    assertThrows(IllegalArgumentException.class, () -> f.obtainFrom(Bean::toString));

    // valid - get
    assertEquals("iValue1", f.obtainFrom(Bean::getiValue1));

    // valid - is
    assertEquals("bValue1", f.obtainFrom(Bean::isbValue1));
  }
}
