package org.summerb.validation.errors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationError;

public class MustNotBeEmpty extends ValidationError {
  private static final long serialVersionUID = 5282624254203784866L;

  public static final String MESSAGE_CODE = "validation.mustNotBe.empty";

  /** @deprecated used only for serialization */
  public MustNotBeEmpty() {}

  public MustNotBeEmpty(@Nonnull String propertyName) {
    super(propertyName, MESSAGE_CODE);
  }
}
