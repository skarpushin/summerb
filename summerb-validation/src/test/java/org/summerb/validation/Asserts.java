package org.summerb.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Asserts {

  /**
   * Just a conventient shortcut for <code>assertThrows(IllegalArgumentException.class, runnable);
   * </code>, it just invokes assertThrows
   *
   * @param runnable runnable
   * @return exception
   */
  public static IllegalArgumentException assertIae(ThrowingRunnable runnable) {
    return assertThrows(IllegalArgumentException.class, runnable::run);
  }

  public static IllegalArgumentException assertIaeMessage(
      String message, ThrowingRunnable runnable) {
    IllegalArgumentException ex = assertIae(runnable);
    assertEquals(message, ex.getMessage());
    return ex;
  }
}
