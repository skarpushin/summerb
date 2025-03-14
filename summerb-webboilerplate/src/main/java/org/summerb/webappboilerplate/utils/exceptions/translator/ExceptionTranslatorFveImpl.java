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

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.summerb.i18n.I18nUtils;
import org.summerb.validation.ValidationError;
import org.summerb.validation.ValidationException;

public class ExceptionTranslatorFveImpl implements ExceptionTranslator {
  protected MessageSource messageSource;

  public ExceptionTranslatorFveImpl(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public String buildUserMessage(Throwable t, Locale locale) {
    if (!ValidationException.class.equals(t.getClass())) {
      return null;
    }
    ValidationException fve = (ValidationException) t;

    StringBuilder ret = new StringBuilder();
    ret.append(I18nUtils.buildMessage(fve, messageSource, locale));
    ret.append(": ");
    boolean first = true;
    for (ValidationError ve : fve.getErrors()) {
      if (!first) {
        ret.append(", ");
      }
      ret.append(translateFieldName(ve.getPropertyName(), messageSource, locale));
      ret.append(" - ");
      ret.append(I18nUtils.buildMessage(ve, messageSource, locale));
      first = false;
    }
    return ret.toString();
  }

  protected static Object translateFieldName(
      String fieldToken, MessageSource messageSource, Locale locale) {
    try {
      return messageSource.getMessage(fieldToken, null, locale);
    } catch (NoSuchMessageException nfe) {
      try {
        return messageSource.getMessage("term." + fieldToken, null, locale);
      } catch (NoSuchMessageException nfe2) {
        return fieldToken;
      }
    }
  }
}
