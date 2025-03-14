package org.summerb.validation.errors;

import java.io.Serial;
import java.util.Arrays;
import java.util.Collection;
import org.summerb.validation.ValidationError;

public class MustBeIn extends ValidationError {
  @Serial private static final long serialVersionUID = -1656837678133781969L;

  public static final String MESSAGE_CODE = "validation.mustBe.in";

  /**
   * @deprecated used only for serialization
   */
  public MustBeIn() {}

  public MustBeIn(String propertyName, Collection<?> values) {
    super(propertyName, MESSAGE_CODE, Arrays.toString(values.toArray()));
  }
}
