package org.summerb.validation.errors;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationError;

public class MustBeIn extends ValidationError {
  private static final long serialVersionUID = -1656837678133781969L;

  public static final String MESSAGE_CODE = "validation.mustBe.in";

  /** @deprecated used only for serialization */
  public MustBeIn() {}

  public MustBeIn(@Nonnull String propertyName, @Nonnull Collection<?> values) {
    super(propertyName, MESSAGE_CODE, Arrays.toString(values.toArray()));
  }
}
