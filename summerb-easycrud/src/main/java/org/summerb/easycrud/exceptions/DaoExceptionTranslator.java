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
package org.summerb.easycrud.exceptions;

import org.summerb.validation.ValidationException;

/**
 * Strategy to translate known DAO-level exception into {@link ValidationException} upon create and
 * update operations
 *
 * <p>NOTE: Name was shortened from DaoExceptionToFieldValidationExceptionTranslator, which seems to
 * be quite long.
 *
 * @author sergeyk
 */
public interface DaoExceptionTranslator {

  /**
   * This method meant to be called from catch clause. If exception cannot be handled by this impl
   * it should just do nothing. Otherwise, it should throw {@link ValidationException} (will be
   * wrapped in unchecked) or any other exception that is applicable
   *
   * @param t exception
   */
  void translateAndThrowIfApplicable(Throwable t);
}
