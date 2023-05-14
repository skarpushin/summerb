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
package org.summerb.utils.exceptions.translator;

import java.util.Locale;

/**
 * Impl of this interface supposed to translate exception into human language and be represented by
 * string.so that we can show it to user
 *
 * @author sergeyk
 */
public interface ExceptionTranslator {
  /**
   * Translate exception into user locale using provided messageSource
   *
   * @return message ready for user OR null if this translator doesn't support this type of
   *     exception
   */
  String buildUserMessage(Throwable t, Locale locale);
}
