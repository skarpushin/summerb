package org.summerb.validation.errors;

import org.summerb.validation.ValidationError;

public class MustBeEmpty extends ValidationError {
  private static final long serialVersionUID = 7898423826957092069L;

  public static final String MESSAGE_CODE = "validation.mustBe.empty";

  /**
   * @deprecated used only for serialization
   */
  public MustBeEmpty() {}

  public MustBeEmpty(String propertyName) {
    super(propertyName, MESSAGE_CODE);
  }
}
