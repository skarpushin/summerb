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
package org.summerb.validation;

import java.util.Collection;

/**
 * This interface was created to facilitate validation of aggregated objects and aggregated object
 * collections through {@link ValidationContext#validateObject(java.util.function.Function,
 * ObjectValidator)} and {@link ValidationContext#validateCollection(java.util.function.Function,
 * ObjectValidator)}
 *
 * @author Sergey Karpushin
 * @param <T> type of validated object
 */
public interface ObjectValidator<T> {

  /**
   * Validate some object
   *
   * @param subject object to be validated
   * @param propertyName name of the property in which this object reference is stored. For
   *     collection items this will be formatted as <code>propertyName[i]</code>
   * @param ctx instance of {@link ValidationContext} that must be used for validating given subject
   * @param optionalSubjectCollection if this subject is a part of collection, this field will
   *     contain reference to such collection
   * @param parentCtx if such validation is performed for aggregated object, then this parameter
   *     will reference parent {@link ValidationContext}
   */
  void validate(
      T subject,
      String propertyName,
      ValidationContext<T> ctx,
      Collection<T> optionalSubjectCollection,
      ValidationContext<?> parentCtx);
}
