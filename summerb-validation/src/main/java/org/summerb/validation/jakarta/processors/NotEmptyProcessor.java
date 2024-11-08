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

import java.util.Collection;
import java.util.Map;

import javax.validation.constraints.NotEmpty;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.errors.MustNotBeEmpty;
import org.summerb.validation.jakarta.processors.abstracts.AnnotationProcessorAbstract;

import com.google.common.base.Preconditions;

public class NotEmptyProcessor extends AnnotationProcessorAbstract<NotEmpty> {

  public NotEmptyProcessor(NotEmpty annotation, String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  public void validate(Object value, ValidationContext<?> ctx) {
    Preconditions.checkArgument(ctx != null, CTX_REQUIRED);

    if (value == null) {
      ctx.notNull(null, propertyName);
      return;
    }

    if (value instanceof CharSequence) {
      String valueStr =
          value instanceof String ? (String) value : ((CharSequence) value).toString();
      ctx.lengthGreater(valueStr, 0, propertyName);
    } else if (value instanceof Collection<?>) {
      ctx.notEmpty((Collection<?>) value, propertyName);
    } else if (value instanceof Map<?, ?>) {
      if (((Map<?, ?>) value).isEmpty()) {
        ctx.add(new MustNotBeEmpty(propertyName));
      }
    } else if (value instanceof Object[]) {
      if (((Object[]) value).length == 0) {
        ctx.add(new MustNotBeEmpty(propertyName));
      }
    } else {
      throw new IllegalArgumentException(
          "Type " + value.getClass() + " is not supported by NotEmpty annotation");
    }
  }
}
