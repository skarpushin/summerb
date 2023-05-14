package org.summerb.validation.errors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationError;

public class MustContain extends ValidationError {
  private static final long serialVersionUID = 7300082983786909455L;

  public static final String MESSAGE_CODE = "validation.must.contain";

  /** @deprecated used only for serialization */
  public MustContain() {}

  public MustContain(@Nonnull String propertyName, @Nonnull String subString) {
    super(propertyName, MESSAGE_CODE, subString);
  }
}
