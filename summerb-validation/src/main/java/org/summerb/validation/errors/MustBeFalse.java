package org.summerb.validation.errors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationError;

public class MustBeFalse extends ValidationError {
  private static final long serialVersionUID = 2318205306618490190L;

  public static final String MESSAGE_CODE = "validation.mustBe.false";

  /** @deprecated used only for serialization */
  public MustBeFalse() {}

  public MustBeFalse(@Nonnull String propertyName) {
    super(propertyName, MESSAGE_CODE);
  }
}
