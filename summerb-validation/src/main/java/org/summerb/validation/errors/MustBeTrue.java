package org.summerb.validation.errors;

import org.summerb.validation.ValidationError;

public class MustBeTrue extends ValidationError {
  private static final long serialVersionUID = 5725044529864502045L;

  public static final String MESSAGE_CODE = "validation.mustBe.true";

  /** @deprecated used only for serialization */
  public MustBeTrue() {}

  public MustBeTrue(String propertyName) {
    super(propertyName, MESSAGE_CODE);
  }
}
