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
package org.summerb.validation.jakarta.processors.abstracts;

import java.lang.annotation.Annotation;

import org.springframework.util.StringUtils;
import org.summerb.validation.jakarta.AnnotationProcessor;

import com.google.common.base.Preconditions;

public abstract class AnnotationProcessorAbstract<T extends Annotation>
    implements AnnotationProcessor<T> {

  protected final T annotation;
  protected final String propertyName;

  public AnnotationProcessorAbstract(T annotation, String propertyName) {
    Preconditions.checkArgument(annotation != null, "annotation required");
    Preconditions.checkArgument(StringUtils.hasText(propertyName), "propertyName required");

    this.annotation = annotation;
    this.propertyName = propertyName;
  }
}
