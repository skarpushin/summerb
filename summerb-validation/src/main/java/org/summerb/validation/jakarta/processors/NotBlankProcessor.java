/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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

import com.google.common.base.Preconditions;
import jakarta.validation.constraints.NotBlank;
import org.summerb.validation.ValidationContext;
import org.summerb.validation.jakarta.processors.abstracts.AnnotationProcessorAbstract;

public class NotBlankProcessor extends AnnotationProcessorAbstract<NotBlank> {

  public NotBlankProcessor(NotBlank annotation, String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  public void validate(Object value, ValidationContext<?> ctx) {
    Preconditions.checkArgument(ctx != null, CTX_REQUIRED);

    if (!ctx.notNull(value, propertyName)) {
      return;
    }

    Preconditions.checkArgument(
        value instanceof CharSequence, "Argument must be of CharSequence type");
    String valueStr = value instanceof String ? (String) value : ((CharSequence) value).toString();
    ctx.hasText(valueStr, propertyName);
  }
}
