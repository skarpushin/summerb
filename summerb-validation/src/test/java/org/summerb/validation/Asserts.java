package org.summerb.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Assert;
import org.junit.function.ThrowingRunnable;

public class Asserts {

  /**
   * Just a conventient shortcut for <code>assertThrows(IllegalArgumentException.class, runnable);
   * </code>, it just invokes {@link Assert#assertThrows(Class, ThrowingRunnable)}
   *
   * @param runnable runnable
   * @return exception
   */
  public static IllegalArgumentException assertIae(ThrowingRunnable runnable) {
    return assertThrows(IllegalArgumentException.class, runnable);
  }

  public static IllegalArgumentException assertIaeMessage(
      String message, ThrowingRunnable runnable) {
    IllegalArgumentException ex = assertIae(runnable);
    assertEquals(message, ex.getMessage());
    return ex;
  }
}
