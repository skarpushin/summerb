/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
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
