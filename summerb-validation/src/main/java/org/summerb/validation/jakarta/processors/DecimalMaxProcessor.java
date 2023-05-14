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

import jakarta.validation.constraints.DecimalMax;

public class DecimalMaxProcessor extends DecimalProcessorNullableAbstract<DecimalMax> {

  private BigDecimal boundary;

  public DecimalMaxProcessor(@Nonnull DecimalMax annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
    boundary = new BigDecimal(annotation.value());
  }

  @Override
  protected void internalValidate(BigDecimal value, ValidationContext<?> ctx) {
    if (annotation.inclusive()) {
      ctx.le(value, boundary, propertyName);
    } else {
      ctx.less(value, boundary, propertyName);
    }
  }
}
