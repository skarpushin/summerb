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

import java.util.List;

/**
 * Impl of this interface supposed to process class to identify all validations that are declared on
 * fields and/or getters/setters.
 *
 * <p>It is advised to wrap actual impl (supposedly {@link JakartaValidationBeanProcessorImpl}) with
 * cached impl, i.e. {@link JakartaValidationBeanProcessorCachedImpl}
 *
 * @author Sergey Karpushin
 */
public interface JakartaValidationBeanProcessor {

  /**
   * @param clazz POJO/Bean class that potentially has Jakarta bean validations applied.
   * @return list of validations found in clazz. Might be empty, never null. Validation annotations
   *     might be found on getters/setters or fields
   */
  List<JakartaValidatorItem> getValidationsFor(Class<?> clazz);
}
