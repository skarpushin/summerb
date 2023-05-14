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
package org.summerb.validation.jakarta;

import java.lang.annotation.Annotation;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.springframework.util.StringUtils;
import org.summerb.validation.ValidationContext;

import com.google.common.base.Preconditions;

public class JakartaValidatorItem {
  private final String propertyName;
  private final Annotation annotation;
  private final AnnotationProcessor<Annotation> processor;
  private final Function<Object, Object> valueGetter;

  public JakartaValidatorItem(
      @Nonnull String propertyName,
      @Nonnull Annotation annotation,
      @Nonnull AnnotationProcessor<Annotation> processor,
      @Nonnull Function<Object, Object> valueGetter) {
    super();

    Preconditions.checkArgument(StringUtils.hasText(propertyName));
    Preconditions.checkArgument(annotation != null);
    Preconditions.checkArgument(processor != null);
    Preconditions.checkArgument(valueGetter != null);

    this.propertyName = propertyName;
    this.annotation = annotation;
    this.processor = processor;
    this.valueGetter = valueGetter;
  }

  public void process(@Nonnull Object bean, @Nonnull ValidationContext<?> ctx) {
    processor.validate(valueGetter.apply(bean), ctx);
  }

  public @Nonnull AnnotationProcessor<Annotation> getProcessor() {
    return processor;
  }

  public @Nonnull Function<Object, Object> getValueGetter() {
    return valueGetter;
  }

  public @Nonnull String getPropertyName() {
    return propertyName;
  }

  public @Nonnull Annotation getAnnotation() {
    return annotation;
  }
}
