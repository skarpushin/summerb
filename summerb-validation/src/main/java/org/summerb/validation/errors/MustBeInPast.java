package org.summerb.validation.errors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationError;

public class MustBeInPast extends ValidationError {
  private static final long serialVersionUID = 3528806988611023493L;

  public static final String MESSAGE_CODE = "validation.mustBe.inPast";

  /** @deprecated used only for serialization */
  public MustBeInPast() {}

  public MustBeInPast(@Nonnull String propertyName) {
    super(propertyName, MESSAGE_CODE);
  }
}
