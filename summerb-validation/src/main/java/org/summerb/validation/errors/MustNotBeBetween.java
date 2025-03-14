package org.summerb.validation.errors;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import java.io.Serial;
import org.summerb.validation.ValidationError;

public class MustNotBeBetween extends ValidationError {
  @Serial private static final long serialVersionUID = 4246268385189876570L;

  public static final String MESSAGE_CODE = "validation.mustNotBe.between";

  /**
   * @deprecated used only for serialization
   */
  public MustNotBeBetween() {}

  public <V> MustNotBeBetween(String propertyName, V lowerBoundary, V upperBoundary) {
    super(propertyName, MESSAGE_CODE, "[", lowerBoundary, upperBoundary, "]");
  }

  public MustNotBeBetween(String propertyName, Range<?> range) {
    super(
        propertyName,
        MESSAGE_CODE,
        range.lowerBoundType() == BoundType.CLOSED ? "[" : "(",
        range.lowerEndpoint(),
        range.upperEndpoint(),
        range.upperBoundType() == BoundType.CLOSED ? "]" : ")");
  }
}
