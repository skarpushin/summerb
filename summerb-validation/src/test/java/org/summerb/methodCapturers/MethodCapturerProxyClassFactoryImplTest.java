package org.summerb.methodCapturers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.summerb.validation.testDtos.Bean;

class MethodCapturerProxyClassFactoryImplTest {

  @Test
  void test_constructor_expectIae() {
    var f =
        new MethodCapturerProxyClassFactoryImpl() {
          @Override
          public Class<MethodCapturer> getProxyType(Class<?> clazz) {
            throw new IllegalStateException("test");
          }
        };

    RuntimeException ex = assertThrows(RuntimeException.class, () -> f.buildProxyFor(Bean.class));
    assertTrue(ex.getMessage().startsWith("Failed to instantiate MethodCapturer for class"));
  }

  @Test
  void test_getProxyType() {
    // illegal case
    var f = new MethodCapturerProxyClassFactoryImpl();
    assertThrows(IllegalArgumentException.class, () -> f.getProxyType(null));

    // ensure no records in map
    assertTrue(f.beanClassToProxyType.isEmpty());

    // valid case
    assertNotNull(f.getProxyType(Bean.class));

    // ensure 1 record in map
    assertEquals(1, f.beanClassToProxyType.size());

    // valid case - ensure returned value non-null when cached (pitest mutation killer)
    assertNotNull(f.getProxyType(Bean.class));
  }

  @Test
  void test_getProxyType_expectCacheWorksCorrectly() {
    var f = new MethodCapturerProxyClassFactoryImpl();

    // ensure no records in map
    assertTrue(f.beanClassToProxyType.isEmpty());

    // trigger addition of item to map
    f.getProxyType(Bean.class);
    Map<Class<?>, Class<MethodCapturer>> map = f.beanClassToProxyType;

    // now let's trigger another time ensure that map will not change (meaning cache worked)
    f.getProxyType(Bean.class);
    assertTrue(map == f.beanClassToProxyType);
  }

  private static class TestMultiThreaded implements Runnable {
    MethodCapturerProxyClassFactoryImpl fixture;
    boolean gotNotNullResult = false;

    public TestMultiThreaded(MethodCapturerProxyClassFactoryImpl fixture) {
      this.fixture = fixture;
    }

    @Override
    public void run() {
      gotNotNullResult = fixture.getProxyType(Bean.class) != null;
    }
  }

  @Test
  void test_getProxyType_multi_threaded_double_check() throws InterruptedException {
    var f =
        new MethodCapturerProxyClassFactoryImpl() {
          Object o = new Object();
          int calls = 0;

          @Override
          protected Class<MethodCapturer> findProxyInCacheForClass(Class<?> clazz) {
            long sleepMs = 0;

            // introducing delays to simulate contingent scenario
            synchronized (o) {
              calls++;
              if (calls == 1) {
                // first call to get from thread A
                sleepMs = 5;
              } else if (calls == 2) {
                // first call to get from thread B
                sleepMs = 10;
              } else if (calls == 3) {
                // second call to get from thread A
                sleepMs = 10;
              }
            }

            if (sleepMs > 0) {
              safeSleep(sleepMs);
            }

            return super.findProxyInCacheForClass(clazz);
          }

          protected void safeSleep(long duration) {
            try {
              Thread.sleep(duration);
            } catch (InterruptedException e) {
              // don't care
            }
          }
        };

    TestMultiThreaded r1 = new TestMultiThreaded(f);
    Thread t1 = new Thread(r1);
    TestMultiThreaded r2 = new TestMultiThreaded(f);
    Thread t2 = new Thread(r2);

    t1.start();
    t2.start();

    t1.join(100);
    t2.join(100);

    assertNotNull(r1.gotNotNullResult);
    assertNotNull(r2.gotNotNullResult);
  }
}
