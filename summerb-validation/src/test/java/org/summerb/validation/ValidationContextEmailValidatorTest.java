package org.summerb.validation;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ValidationContextEmailValidatorTest {

  /** Examples are taken from https://en.wikipedia.org/wiki/Email_address */
  @ParameterizedTest
  @ValueSource(
      strings = {
        "very.common@example.com",
        "disposable.style.email.with+symbol@example.com",
        "other.email-with-hyphen@example.com",
        "fully-qualified-domain@example.com",
        "user.name+tag+sorting@example.com",
        "x@example.com",
        "example-indeed@strange-example.com",
        "test/test@test.com",
        "example@s.example",
        "\"john..doe\"@example.org",
        "mailhost!username@example.org",
        "user%example.com@example.org",
        "user-@example.org",
        "postmaster@[123.123.123.123]",
        "veryMuchValid@email.com" // capital case characters
      })
  public void testValidateEmailFormat_expectOkForValidEmails(String email) {
    assertEquals(true, ValidationContext.isValidEmail(email));
  }

  /** Examples are taken from https://en.wikipedia.org/wiki/Email_address */
  @ParameterizedTest
  @ValueSource(
      strings = {
        "Abc.example.com",
        "A@b@c@example.com",
        "a\"b(c)d,e:f;g<h>i[j\\k]l@example.com",
        "just\"not\"right@example.com",
        "this is\"not\\allowed@example.com",
        "this\\ still\\\"not\\\\allowed@example.com",
        "i_like_underscore@but_its_not_allowed_in_this_part.example.com"
      })
  public void testValidateEmailFormat_expectFailForInvalidEmails(String email) {
    assertEquals(false, ValidationContext.isValidEmail(email));
  }
}
