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

/**
 * This translator treats class name as a message code, and its message as a first argument message
 *
 * @author sergeyk
 */
public class ExceptionTranslatorClassNameImpl implements ExceptionTranslator {
  protected MessageSource messageSource;

  public ExceptionTranslatorClassNameImpl(MessageSource messageSource2) {
    messageSource = messageSource2;
  }

  @Override
  public String buildUserMessage(Throwable t, Locale locale) {
    try {
      String className = t.getClass().getName();
      return messageSource.getMessage(className, new Object[] {t.getMessage()}, locale);
    } catch (NoSuchMessageException nfe) {
      return t.getClass().getSimpleName() + " (" + t.getLocalizedMessage() + ")";
    }
  }
}
