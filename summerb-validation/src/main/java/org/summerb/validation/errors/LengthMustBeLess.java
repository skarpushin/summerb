package org.summerb.validation.errors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationError;

public class LengthMustBeLess extends ValidationError {
  private static final long serialVersionUID = 5345418840176533746L;

  public static final String MESSAGE_CODE = "validation.length.mustBe.less";

  /** @deprecated used only for serialization */
  public LengthMustBeLess() {}

  public LengthMustBeLess(@Nonnull String propertyName, int boundary) {
    super(propertyName, MESSAGE_CODE, boundary);
  }
}
