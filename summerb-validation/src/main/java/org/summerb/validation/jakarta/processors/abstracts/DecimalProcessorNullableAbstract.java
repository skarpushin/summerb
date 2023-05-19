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
import java.util.HashSet;
import java.util.Set;

import org.summerb.validation.ValidationContext;

import com.google.common.base.Preconditions;

public abstract class DecimalProcessorNullableAbstract<T extends Annotation>
    extends AnnotationProcessorNullableAbstract<T> {

  public static final Set<Class<?>> ALLOWED_TYPES;

  static {
    Set<Class<?>> allowed = new HashSet<>();
    allowed.add(BigDecimal.class);
    allowed.add(BigInteger.class);
    allowed.add(CharSequence.class);
    allowed.add(Byte.class);
    allowed.add(Short.class);
    allowed.add(Integer.class);
    allowed.add(Long.class);
    ALLOWED_TYPES = allowed;
  }

  public DecimalProcessorNullableAbstract(T annotation, String propertyName) {
    super(annotation, propertyName);
  }

  @Override
  protected void internalValidate(Object value, ValidationContext<?> ctx) {
    Class<? extends Object> valueType = value.getClass();
    Preconditions.checkArgument(
        ALLOWED_TYPES.contains(valueType) || value instanceof CharSequence,
        "Type is not one of allowed %s: %s",
        ALLOWED_TYPES,
        valueType);

    internalValidate(new BigDecimal(String.valueOf(value)), ctx);
  }

  protected abstract void internalValidate(BigDecimal valueToValidate, ValidationContext<?> ctx);
}
