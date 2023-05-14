package org.summerb.validation.errors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationError;

public class MustBeInPastOrPresent extends ValidationError {
  private static final long serialVersionUID = -8228585741561780676L;

  public static final String MESSAGE_CODE = "validation.mustBe.inPastOrPresent";

  /** @deprecated used only for serialization */
  public MustBeInPastOrPresent() {}

  public MustBeInPastOrPresent(@Nonnull String propertyName) {
    super(propertyName, MESSAGE_CODE);
  }
}
