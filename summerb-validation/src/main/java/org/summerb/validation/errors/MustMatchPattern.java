package org.summerb.validation.errors;

import org.summerb.validation.ValidationError;

public class MustMatchPattern extends ValidationError {
  private static final long serialVersionUID = -8383938049591309500L;

  public static final String MESSAGE_CODE = "validation.must.matchPattern";

  /** @deprecated used only for serialization */
  public MustMatchPattern() {}

  public MustMatchPattern(String propertyName, String messageCode) {
    super(propertyName, messageCode);
  }
}
