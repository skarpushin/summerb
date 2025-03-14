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
package org.summerb.validation.jakarta;

import jakarta.validation.constraints.AssertTrue;
import java.lang.annotation.Annotation;
import org.summerb.validation.ValidationContext;

/**
 * Impl of this interface is supposed to be stateful (instance created for each annotation on the
 * bean)
 *
 * @author Sergey Karpushin
 * @param <T> type of annotation, one of jakarta validation constraints. I.e. {@link AssertTrue}
 */
public interface AnnotationProcessor<T extends Annotation> {
  String CTX_REQUIRED = "ctx required";

  void validate(Object value, ValidationContext<?> ctx);
}
