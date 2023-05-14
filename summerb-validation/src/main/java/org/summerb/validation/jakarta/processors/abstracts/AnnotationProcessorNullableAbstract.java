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
package org.summerb.validation.jakarta.processors.abstracts;

import java.lang.annotation.Annotation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.summerb.validation.ValidationContext;

import com.google.common.base.Preconditions;

/**
 * Abstract class for processors of constraints annotations which treat null value is valid
 *
 * @author Sergey Karpushin
 * @param <T> type of Annotation
 */
public abstract class AnnotationProcessorNullableAbstract<T extends Annotation>
    extends AnnotationProcessorAbstract<T> {

  public AnnotationProcessorNullableAbstract(@Nonnull T annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  public void validate(@Nullable Object value, @Nonnull ValidationContext<?> ctx) {
    Preconditions.checkArgument(ctx != null, CTX_REQUIRED);

    if (value == null) {
      // NOTE: As per jakarta validation, null values are considered valid (for some of the
      // constraints)
      return;
    }

    internalValidate(value, ctx);
  }

  protected abstract void internalValidate(Object value, ValidationContext<?> ctx);
}
