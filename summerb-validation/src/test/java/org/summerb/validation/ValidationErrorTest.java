package org.summerb.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;
import org.summerb.validation.gson.ValidationErrorGsonTypeAdapter;

class ValidationErrorTest {

  // NOTE: Other tests are not created here because same functionality is already covered by
  // ValidationContextTest -- so we only add tests that are needed to complete coverage/mutational
  // coverage

  @Test
  void test_Constructor() {
    assertThrows(IllegalArgumentException.class, () -> new ValidationError("asd", null));
    assertThrows(IllegalArgumentException.class, () -> new ValidationError("asd", ""));
    assertThrows(IllegalArgumentException.class, () -> new ValidationError("asd", " "));

    assertThrows(IllegalArgumentException.class, () -> new ValidationError(null, "asd"));
    assertThrows(IllegalArgumentException.class, () -> new ValidationError("", "asd"));
    assertThrows(IllegalArgumentException.class, () -> new ValidationError(" ", "asd"));
  }

  @Test
  void test_ToString() {
    ValidationError ve = new ValidationError("pn", "mc", "arg1", 2);
    assertEquals("pn: code = 'mc', args = [arg1, 2]", ve.toString());
  }

  @Test
  void test_args() {
    // should be accepted ok
    Object arg = null;
    new ValidationError("pn", "mc", arg);

    // should be accepted ok
    new ValidationError("pn", "mc", null, null);

    // translated to toString
    ValidationError validationError =
        new ValidationError("pn", "mc", LocalDate.of(2025, Month.APRIL, 1));
    assertEquals("2025-04-01", validationError.getMessageArgs()[0].toString());
  }

  @Test
  void test_io() {
    // should be accepted ok
    ValidationError ve =
        new ValidationError(
            "pn",
            "mc",
            BigDecimal.ZERO,
            (float) 1,
            (double) 1,
            (long) 1,
            (short) 1,
            true,
            BigInteger.valueOf(1),
            "asd",
            (byte) 1,
            1);

    Gson gson =
        new GsonBuilder()
            .registerTypeAdapter(ValidationError.class, new ValidationErrorGsonTypeAdapter())
            .create();

    String json = gson.toJson(ve);
    ve = gson.fromJson(json, ValidationError.class);

    assertEquals(BigDecimal.class, ve.getMessageArgs()[0].getClass());
    assertEquals(Float.class, ve.getMessageArgs()[1].getClass());
    assertEquals(Double.class, ve.getMessageArgs()[2].getClass());
    assertEquals(Long.class, ve.getMessageArgs()[3].getClass());
    assertEquals(Short.class, ve.getMessageArgs()[4].getClass());
    assertEquals(Boolean.class, ve.getMessageArgs()[5].getClass());
    assertEquals(BigInteger.class, ve.getMessageArgs()[6].getClass());
    assertEquals(String.class, ve.getMessageArgs()[7].getClass());
    assertEquals(Byte.class, ve.getMessageArgs()[8].getClass());
  }

  @Test
  void test_ensureCannotModifyMessageArgs() {
    ValidationError ve = new ValidationError("pn", "mc", 1, 2);
    ve.getMessageArgs()[0] = 555;
    assertEquals(1, ve.getMessageArgs()[0]);
  }

  @Test
  void test_messageArgs_expectCanSetNullArgs() {
    ValidationError ve = new ValidationError("pn", "mc", 1, 2);
    ve.setMessageArgs(null);
  }
}
