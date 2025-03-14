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
package org.summerb.webappboilerplate.utils.exceptions.translator;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.MessageSource;

public class ExceptionTranslatorLegacyImpl extends ExceptionTranslatorDelegatingImpl {
  public ExceptionTranslatorLegacyImpl(MessageSource messageSource) {
    super(buildLegacyTranslatorsList(messageSource));
  }

  public static List<ExceptionTranslator> buildLegacyTranslatorsList(MessageSource messageSource) {
    return Arrays.asList(
        new ExceptionTranslatorFveImpl(messageSource),
        new ExceptionTranslatorHasMessageImpl(messageSource),
        new ExceptionTranslatorClassNameImpl(messageSource));
  }
}
