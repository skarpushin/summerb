package org.summerb.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ValidationErrorsUtilsTest {

  // NOTE: Other tests are not created here because same functionality is already covered by
  // ValidationContextTest -- so we only add tests that are needed to complete coverage/mutational
  // coverage

  @Test
  void testFindErrorsForField() {
    assertThrows(
        IllegalArgumentException.class, () -> ValidationErrorsUtils.findErrorsForField(null, null));
  }
}
