package org.summerb.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.summerb.validation.errors.MustBeEmpty;
import org.summerb.validation.errors.MustBeGreater;
import org.summerb.validation.errors.MustHaveText;

class ValidationExceptionTest {

  @Test
  void test_constructor_expectIaeOnInvalidArgs() {
    ValidationError error = null;
    assertThrows(IllegalArgumentException.class, () -> new ValidationException(error));

    List<ValidationError> errorsList = null;
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> new ValidationException(errorsList));
    assertEquals("validationErrors required", ex.getMessage());

    ValidationErrors errors = null;
    assertThrows(IllegalArgumentException.class, () -> new ValidationException(errors));

    ValidationErrors errorsEmpty = new ValidationErrors();
    assertThrows(IllegalArgumentException.class, () -> new ValidationException(errorsEmpty));
  }

  @Test
  void test_constructor() {
    var f =
        new ValidationException(
            Arrays.asList(new MustHaveText("asd"), new MustBeGreater("fgh", 1)));
    assertEquals(2, f.getErrors().size());
    assertEquals(2, f.getErrorDescriptionObject().getList().size());
    assertEquals(ValidationException.MESSAGE_CODE, f.getMessageCode());

    assertNotNull(f.findErrorOfType(MustHaveText.class));
    assertNull(f.findErrorOfType(MustBeEmpty.class));

    assertTrue(f.hasErrorOfType(MustHaveText.class));
    assertFalse(f.hasErrorOfType(MustBeEmpty.class));

    assertNotNull(f.findErrorOfTypeForField(MustHaveText.class, "asd"));
    assertNull(f.findErrorOfTypeForField(MustBeEmpty.class, "qqq"));

    assertEquals(0, f.findErrorsForField("qqq").size());
    assertEquals(1, f.findErrorsForField("asd").size());

    f = new ValidationException(new MustHaveText("asd"));
    assertEquals(1, f.getErrors().size());
    assertEquals(1, f.getErrorDescriptionObject().getList().size());

    f =
        new ValidationException(
            new ValidationErrors(
                Arrays.asList(new MustHaveText("asd"), new MustBeGreater("fgh", 1))));
    assertEquals(2, f.getErrors().size());
    assertEquals(2, f.getErrorDescriptionObject().getList().size());
  }

  @Test
  void test_toString() {
    var f =
        new ValidationException(
            Arrays.asList(new MustHaveText("asd"), new MustBeGreater("fgh", 1)));

    assertEquals(
        "validation.error: \n"
            + "	asd: code = 'validation.must.haveText'\n"
            + "	fgh: code = 'validation.mustBe.greater', args = [1]",
        f.toString());
  }
}
