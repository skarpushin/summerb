package org.summerb.validation.errors;

import java.io.Serial;
import org.summerb.validation.ValidationError;

public class MustStartWith extends ValidationError {
  @Serial private static final long serialVersionUID = 4618610743766324351L;

  public static final String MESSAGE_CODE = "validation.must.startWith";

  /**
   * @deprecated used only for serialization
   */
  public MustStartWith() {}

  public MustStartWith(String propertyName, String subString) {
    super(propertyName, MESSAGE_CODE, subString);
  }
}
