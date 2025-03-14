package org.summerb.validation.errors;

import java.io.Serial;
import org.summerb.validation.ValidationError;

public class MustBeInFuture extends ValidationError {
  @Serial private static final long serialVersionUID = -8562776462579734935L;

  public static final String MESSAGE_CODE = "validation.mustBe.inFuture";

  /**
   * @deprecated used only for serialization
   */
  public MustBeInFuture() {}

  public MustBeInFuture(String propertyName) {
    super(propertyName, MESSAGE_CODE);
  }
}
