package org.summerb.validation.errors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationError;

public class LengthMustBeBetween extends ValidationError {
  private static final long serialVersionUID = 8850577964582822522L;

  public static final String MESSAGE_CODE = "validation.length.mustBe.between";

  /** @deprecated used only for serialization */
  public LengthMustBeBetween() {}

  public LengthMustBeBetween(@Nonnull String propertyName, int lowerBoundary, int upperBoundary) {
    super(propertyName, MESSAGE_CODE, lowerBoundary, upperBoundary);
  }
}
