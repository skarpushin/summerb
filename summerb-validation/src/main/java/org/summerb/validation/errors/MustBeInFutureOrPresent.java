package org.summerb.validation.errors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationError;

public class MustBeInFutureOrPresent extends ValidationError {
  private static final long serialVersionUID = -2674456739344106167L;

  public static final String MESSAGE_CODE = "validation.mustBe.inFutureOrPresent";

  /** @deprecated used only for serialization */
  public MustBeInFutureOrPresent() {}

  public MustBeInFutureOrPresent(@Nonnull String propertyName) {
    super(propertyName, MESSAGE_CODE);
  }
}
