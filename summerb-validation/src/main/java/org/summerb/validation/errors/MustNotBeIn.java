package org.summerb.validation.errors;

import java.io.Serial;
import java.util.Arrays;
import java.util.Collection;
import org.summerb.validation.ValidationError;

public class MustNotBeIn extends ValidationError {
  @Serial private static final long serialVersionUID = 7267869071746060380L;

  public static final String MESSAGE_CODE = "validation.mustNotBe.in";

  /**
   * @deprecated used only for serialization
   */
  public MustNotBeIn() {}

  public MustNotBeIn(String propertyName, Collection<?> values) {
    super(propertyName, MESSAGE_CODE, Arrays.toString(values.toArray()));
  }
}
