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
package org.summerb.webappboilerplate.utils.exceptions.translator;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.summerb.i18n.HasMessageCode;
import org.summerb.i18n.I18nUtils;

public class ExceptionTranslatorHasMessageImpl implements ExceptionTranslator {
  protected MessageSource messageSource;

  public ExceptionTranslatorHasMessageImpl(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public String buildUserMessage(Throwable t, Locale locale) {
    if (!HasMessageCode.class.isAssignableFrom(t.getClass())) {
      return null;
    }
    HasMessageCode hasMessage = (HasMessageCode) t;
    return I18nUtils.buildMessage(hasMessage, messageSource, locale);
  }
}
