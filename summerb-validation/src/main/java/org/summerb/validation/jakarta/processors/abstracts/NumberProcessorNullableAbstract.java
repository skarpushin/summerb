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
import java.math.BigDecimal;
import java.math.BigInteger;

import org.summerb.validation.ValidationContext;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public abstract class NumberProcessorNullableAbstract<T extends Annotation>
    extends AnnotationProcessorNullableAbstract<T> {

  public NumberProcessorNullableAbstract(T annotation, String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  protected void internalValidate(Object value, ValidationContext<?> ctx) {
    Preconditions.checkArgument(value instanceof Number, "value must be of type Number");
    internalValidate(getSignum((Number) value), ctx);
  }

  @VisibleForTesting
  public static int getSignum(Number value) {
    if (value instanceof BigDecimal) {
      return ((BigDecimal) value).signum();
    } else if (value instanceof BigInteger) {
      return ((BigInteger) value).signum();
    } else if (value instanceof Byte
        || value instanceof Integer
        || value instanceof Long
        || value instanceof Short) {
      long val = value.longValue();
      return val < 0 ? -1 : (val > 0 ? 1 : 0);
    } else if (value instanceof Double || value instanceof Float) {
      double val = value.doubleValue();
      return val < 0 ? -1 : (val > 0 ? 1 : 0);
    } else {
      throw new IllegalArgumentException("Type is not supported: " + value.getClass());
    }
  }

  protected abstract void internalValidate(int signum, ValidationContext<?> ctx);
}
