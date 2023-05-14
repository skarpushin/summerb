package org.summerb.validation.jakarta.processors.abstracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.summerb.validation.jakarta.processors.abstracts.NumberProcessorNullableAbstract.getSignum;

import org.junit.jupiter.api.Test;

class NumberProcessorNullableAbstractTest {

  @Test
  void test_getSignum_expectCorrectResult() {
    assertEquals(0, getSignum(0));
    assertEquals(-1, getSignum(-100));
    assertEquals(1, getSignum(100));

    assertEquals(0, getSignum(0f));
    assertEquals(-1, getSignum(-100f));
    assertEquals(1, getSignum(100f));
  }
}
