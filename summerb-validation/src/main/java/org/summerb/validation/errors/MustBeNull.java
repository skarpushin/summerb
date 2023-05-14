package org.summerb.validation.errors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationError;

public class MustBeNull extends ValidationError {
  private static final long serialVersionUID = 6647091549196157385L;

  public static final String MESSAGE_CODE = "validation.mustBe.null";

  /** @deprecated used only for serialization */
  public MustBeNull() {}

  public MustBeNull(@Nonnull String propertyName) {
    super(propertyName, MESSAGE_CODE);
  }
}
