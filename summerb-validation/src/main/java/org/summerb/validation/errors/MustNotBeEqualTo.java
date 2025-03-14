package org.summerb.validation.errors;

import java.io.Serial;
import org.summerb.validation.ValidationError;

public class MustNotBeEqualTo extends ValidationError {
  @Serial private static final long serialVersionUID = 255121807437076870L;

  public static final String MESSAGE_CODE = "validation.mustNotBe.equal";

  /**
   * @deprecated used only for serialization
   */
  public MustNotBeEqualTo() {}

  public MustNotBeEqualTo(String propertyName, Object value) {
    super(propertyName, MESSAGE_CODE, value);
  }
}
