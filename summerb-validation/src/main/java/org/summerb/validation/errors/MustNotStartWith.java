package org.summerb.validation.errors;

import org.summerb.validation.ValidationError;

public class MustNotStartWith extends ValidationError {
  private static final long serialVersionUID = 627529742498471905L;

  public static final String MESSAGE_CODE = "validation.mustNot.startWith";

  /** @deprecated used only for serialization */
  public MustNotStartWith() {}

  public MustNotStartWith(String propertyName, String subString) {
    super(propertyName, MESSAGE_CODE, subString);
  }
}
