package org.summerb.validation.errors;

import java.io.Serial;
import org.summerb.validation.ValidationError;

public class LengthMustBeLess extends ValidationError {
  @Serial private static final long serialVersionUID = 5345418840176533746L;

  public static final String MESSAGE_CODE = "validation.length.mustBe.less";

  /**
   * @deprecated used only for serialization
   */
  public LengthMustBeLess() {}

  public LengthMustBeLess(String propertyName, int boundary) {
    super(propertyName, MESSAGE_CODE, boundary);
  }
}
