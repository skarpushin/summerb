package org.summerb.validation.errors;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationError;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

public class MustNotBeBetween extends ValidationError {
  private static final long serialVersionUID = 4246268385189876570L;

  public static final String MESSAGE_CODE = "validation.mustNotBe.between";

  /** @deprecated used only for serialization */
  public MustNotBeBetween() {}

  public <V> MustNotBeBetween(
      @Nonnull String propertyName, @Nonnull V lowerBoundary, @Nonnull V upperBoundary) {
    super(propertyName, MESSAGE_CODE, "[", lowerBoundary, upperBoundary, "]");
  }

  public MustNotBeBetween(@Nonnull String propertyName, @Nonnull Range<?> range) {
    super(
        propertyName,
        MESSAGE_CODE,
        range.lowerBoundType() == BoundType.CLOSED ? "[" : "(",
        range.lowerEndpoint(),
        range.upperEndpoint(),
        range.upperBoundType() == BoundType.CLOSED ? "]" : ")");
  }
}
