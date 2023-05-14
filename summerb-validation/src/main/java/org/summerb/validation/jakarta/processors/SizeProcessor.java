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

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.errors.LengthMustBeBetween;
import org.summerb.validation.jakarta.processors.abstracts.AnnotationProcessorNullableAbstract;

import jakarta.validation.constraints.Size;

public class SizeProcessor extends AnnotationProcessorNullableAbstract<Size> {

  public SizeProcessor(@Nonnull Size annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  protected void internalValidate(Object value, ValidationContext<?> ctx) {
    int len;
    if (value instanceof CharSequence) {
      len = ((CharSequence) value).length();
    } else if (value instanceof Collection<?>) {
      len = ((Collection<?>) value).size();
    } else if (value instanceof Map<?, ?>) {
      len = ((Map<?, ?>) value).size();
    } else if (value instanceof Object[]) {
      len = ((Object[]) value).length;
    } else {
      throw new IllegalArgumentException(
          "Type " + value.getClass() + " is not supported by Size annotation");
    }

    if (annotation.min() <= len && len <= annotation.max()) {
      return;
    }
    ctx.add(new LengthMustBeBetween(propertyName, annotation.min(), annotation.max()));
  }
}
