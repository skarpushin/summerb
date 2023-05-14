package org.summerb.validation.errors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationError;

public class MustEndWith extends ValidationError {
  private static final long serialVersionUID = -2860317584563253244L;

  public static final String MESSAGE_CODE = "validation.must.endWith";

  /** @deprecated used only for serialization */
  public MustEndWith() {}

  public MustEndWith(@Nonnull String propertyName, @Nonnull String subString) {
    super(propertyName, MESSAGE_CODE, subString);
  }
}
