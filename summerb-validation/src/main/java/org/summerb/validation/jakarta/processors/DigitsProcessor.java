package org.summerb.validation.jakarta.processors;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.jakarta.processors.abstracts.DecimalProcessorNullableAbstract;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import jakarta.validation.constraints.Digits;

public class DigitsProcessor extends DecimalProcessorNullableAbstract<Digits> {

  protected BigDecimal boundary;

  public DigitsProcessor(@Nonnull Digits annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
    boundary = buildBoundary(annotation.integer(), annotation.fraction());
  }

  @Override
  protected void internalValidate(BigDecimal value, ValidationContext<?> ctx) {
    ctx.le(value, boundary, propertyName);
  }

  @VisibleForTesting
  protected static BigDecimal buildBoundary(int integer, int fraction) {
    Preconditions.checkArgument(integer > 0, "integer part must be positive: %s", integer);
    Preconditions.checkArgument(fraction >= 0, "fraction part must be non-negative: %s", fraction);

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < integer; i++) {
      sb.append("9");
    }

    for (int i = 0; i < fraction; i++) {
      if (i == 0) {
        sb.append(".");
      }
      sb.append("9");
    }

    return new BigDecimal(sb.toString());
  }
}
