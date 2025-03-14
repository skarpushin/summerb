package org.summerb.validation.errors;

import java.io.Serial;
import org.summerb.validation.ValidationError;

public class MustEndWith extends ValidationError {
  @Serial private static final long serialVersionUID = -2860317584563253244L;

  public static final String MESSAGE_CODE = "validation.must.endWith";

  /**
   * @deprecated used only for serialization
   */
  public MustEndWith() {}

  public MustEndWith(String propertyName, String subString) {
    super(propertyName, MESSAGE_CODE, subString);
  }
}
