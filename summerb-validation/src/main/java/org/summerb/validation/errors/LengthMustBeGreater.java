package org.summerb.validation.errors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationError;

public class LengthMustBeGreater extends ValidationError {
  private static final long serialVersionUID = 6256485543765469657L;

  public static final String MESSAGE_CODE = "validation.length.mustBe.greater";

  /** @deprecated used only for serialization */
  public LengthMustBeGreater() {}

  public LengthMustBeGreater(@Nonnull String propertyName, int boundary) {
    super(propertyName, MESSAGE_CODE, boundary);
  }
}
