package org.summerb.validation.jakarta.processors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.summerb.validation.Asserts.assertIae;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class DigitsProcessorTest {

  @Test
  void test_buildBoundary_expectIae() {
    IllegalArgumentException ex = assertIae(() -> DigitsProcessor.buildBoundary(0, 0));
    assertTrue(ex.getMessage().startsWith("integer part must be positive"));
    ex = assertIae(() -> DigitsProcessor.buildBoundary(-1, 0));
    assertTrue(ex.getMessage().startsWith("integer part must be positive"));

    assertThrows(IllegalArgumentException.class, () -> DigitsProcessor.buildBoundary(1, -1));
  }

  @Test
  void test_buildBoundary_expectCorrectOutput() {
    BigDecimal result = DigitsProcessor.buildBoundary(3, 0);
    assertEquals("999", result.toString());

    result = DigitsProcessor.buildBoundary(3, 2);
    assertEquals("999.99", result.toString());
  }
}
