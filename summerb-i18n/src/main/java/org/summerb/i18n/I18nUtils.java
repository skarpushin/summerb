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
package org.summerb.i18n;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

// TBD: Consider implementing customizable (based on strategies) regular bean instead of this
// singleton impl with fixed behavior

public abstract class I18nUtils {
  public static String buildMessage(
      HasMessageCode hasMessageCode, MessageSource messageSource, Locale locale) {
    try {
      Object[] args = null;
      if (hasMessageCode instanceof HasMessageArgs) {
        args = ((HasMessageArgs) hasMessageCode).getMessageArgs();
      }

      MessageArgConverter[] argsConverters = null;
      if (hasMessageCode instanceof HasMessageArgsConverters) {
        argsConverters = ((HasMessageArgsConverters) hasMessageCode).getMessageArgsConverters();
      }

      if (args != null && argsConverters != null) {
        applyArgsConversion(hasMessageCode, args, argsConverters, messageSource, locale);
      }

      return getMessage(hasMessageCode.getMessageCode(), args, messageSource, locale);
    } catch (Throwable t) {
      throw new RuntimeException("Failed to build message", t);
    }
  }

  protected static void applyArgsConversion(
      HasMessageCode hasMessageCode,
      Object[] args,
      MessageArgConverter[] argsConverters,
      MessageSource messageSource,
      Locale locale) {
    for (int i = 0; i < args.length; i++) {
      if (argsConverters.length < i + 1) {
        // there is no more converters, nothing to convert
        break;
      }
      if (argsConverters[i] == null) {
        continue;
      }

      args[i] =
          args[i] == null
              ? "(null)"
              : argsConverters[i].convert(args[i], hasMessageCode, messageSource, locale);
    }
  }

  protected static String getMessage(
      String messageCode, Object[] args, MessageSource messageSource, Locale locale) {
    try {
      return messageSource.getMessage(messageCode, args, locale);
    } catch (NoSuchMessageException nsme) {
      // as a backup plan just return message code
      return messageCode;
    }
  }
}
