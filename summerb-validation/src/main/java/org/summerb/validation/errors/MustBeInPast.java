package org.summerb.validation.errors;

import java.io.Serial;
import org.summerb.validation.ValidationError;

public class MustBeInPast extends ValidationError {
  @Serial private static final long serialVersionUID = 3528806988611023493L;

  public static final String MESSAGE_CODE = "validation.mustBe.inPast";

  /**
   * @deprecated used only for serialization
   */
  public MustBeInPast() {}

  public MustBeInPast(String propertyName) {
    super(propertyName, MESSAGE_CODE);
  }
}
