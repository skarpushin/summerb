package org.summerb.validation.errors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationError;

public class MustStartWith extends ValidationError {
  private static final long serialVersionUID = 4618610743766324351L;

  public static final String MESSAGE_CODE = "validation.must.startWith";

  /** @deprecated used only for serialization */
  public MustStartWith() {}

  public MustStartWith(@Nonnull String propertyName, @Nonnull String subString) {
    super(propertyName, MESSAGE_CODE, subString);
  }
}
