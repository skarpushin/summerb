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
package org.summerb.validation.jakarta;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Registry of {@link AnnotationProcessor}.
 *
 * <p>p.s. It does both - identification and instantiation which seem at first as OOD:ISP/SRP
 * violation, but identification is closely related to checking if correct constructor is present,
 * which leads us to instantiation, hence I've decided to live with this for some time
 */
public interface JakartaAnnotationsProcessorsRegistry {

  Set<Class<? extends Annotation>> getSupportedAnnotations();

  <T extends Annotation> AnnotationProcessor<T> buildAnnotationProcessor(
      T annotation, String propertyName);
}
