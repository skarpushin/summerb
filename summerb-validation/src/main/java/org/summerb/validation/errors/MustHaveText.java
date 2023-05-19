package org.summerb.validation.errors;

import org.summerb.validation.ValidationError;

public class MustHaveText extends ValidationError {
  private static final long serialVersionUID = 8910920019747001781L;

  public static final String MESSAGE_CODE = "validation.must.haveText";

  /** @deprecated used only for serialization */
  public MustHaveText() {}

  public MustHaveText(String propertyName) {
    super(propertyName, MESSAGE_CODE);
  }
}
