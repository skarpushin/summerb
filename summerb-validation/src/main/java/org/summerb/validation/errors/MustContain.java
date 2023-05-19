package org.summerb.validation.errors;

import org.summerb.validation.ValidationError;

public class MustContain extends ValidationError {
  private static final long serialVersionUID = 7300082983786909455L;

  public static final String MESSAGE_CODE = "validation.must.contain";

  /** @deprecated used only for serialization */
  public MustContain() {}

  public MustContain(String propertyName, String subString) {
    super(propertyName, MESSAGE_CODE, subString);
  }
}
