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
package org.summerb.webappboilerplate.utils.exceptions.translator;

/**
 * This interface is similar to {@link ExceptionTranslator} with only exception - impl supposed to
 * know how to resolve locale
 *
 * @author sergeyk
 */
public interface ExceptionTranslatorSimplified {
  /**
   * Translate exception into user locale using provided messageSource
   *
   * @return message ready for user OR null if this translator doesn't support this type of
   *     exception
   */
  String buildUserMessage(Throwable t);
}
