package org.summerb.validation.errors;

import java.io.Serial;
import org.summerb.validation.ValidationError;

public class MustNotContain extends ValidationError {
  @Serial private static final long serialVersionUID = -6648038057849859379L;

  public static final String MESSAGE_CODE = "validation.mustNot.contain";

  /**
   * @deprecated used only for serialization
   */
  public MustNotContain() {}

  public MustNotContain(String propertyName, String subString) {
    super(propertyName, MESSAGE_CODE, subString);
  }
}
