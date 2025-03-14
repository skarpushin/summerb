package org.summerb.validation.errors;

import java.io.Serial;
import org.summerb.validation.ValidationError;

public class MustBeInPastOrPresent extends ValidationError {
  @Serial private static final long serialVersionUID = -8228585741561780676L;

  public static final String MESSAGE_CODE = "validation.mustBe.inPastOrPresent";

  /**
   * @deprecated used only for serialization
   */
  public MustBeInPastOrPresent() {}

  public MustBeInPastOrPresent(String propertyName) {
    super(propertyName, MESSAGE_CODE);
  }
}
