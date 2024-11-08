/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
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

import javax.validation.constraints.Min;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.jakarta.processors.abstracts.DecimalProcessorNullableAbstract;

public class MinProcessor extends DecimalProcessorNullableAbstract<Min> {

  public MinProcessor(Min annotation, String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  protected void internalValidate(BigDecimal value, ValidationContext<?> ctx) {
    ctx.ge(value.longValue(), annotation.value(), propertyName);
  }
}
