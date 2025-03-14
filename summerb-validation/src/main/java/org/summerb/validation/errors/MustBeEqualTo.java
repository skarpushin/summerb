package org.summerb.validation.errors;

import java.io.Serial;
import org.summerb.validation.ValidationError;

public class MustBeEqualTo extends ValidationError {
  @Serial private static final long serialVersionUID = -3143603791745615037L;

  public static final String MESSAGE_CODE = "validation.mustBe.equalTo";

  /**
   * @deprecated used only for serialization
   */
  public MustBeEqualTo() {}

  public MustBeEqualTo(String propertyName, Object expectedValue) {
    super(propertyName, MESSAGE_CODE, expectedValue);
  }
}
