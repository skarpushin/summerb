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

import javax.validation.constraints.PastOrPresent;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.jakarta.processors.abstracts.AnnotationProcessorNullableAbstract;

import com.google.common.base.Preconditions;

public class PastOrPresentProcessor extends AnnotationProcessorNullableAbstract<PastOrPresent> {

  public PastOrPresentProcessor(PastOrPresent annotation, String propertyName) {
    super(annotation, propertyName);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  protected void internalValidate(Object value, ValidationContext<?> ctx) {
    Preconditions.checkArgument(
        value instanceof Comparable, "value must be at least of Comparable type");
    ctx.pastOrPresent((Comparable) value, propertyName);
  }
}
