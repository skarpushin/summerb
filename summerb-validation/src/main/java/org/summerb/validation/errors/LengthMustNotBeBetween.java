package org.summerb.validation.errors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationError;

public class LengthMustNotBeBetween extends ValidationError {
  private static final long serialVersionUID = 6291582559911847149L;

  public static final String MESSAGE_CODE = "validation.length.mustNotBe.between";

  /** @deprecated used only for serialization */
  public LengthMustNotBeBetween() {}

  public LengthMustNotBeBetween(
      @Nonnull String propertyName, int lowerBoundary, int upperBoundary) {
    super(propertyName, MESSAGE_CODE, lowerBoundary, upperBoundary);
  }
}
