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
package org.summerb.i18n;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.google.common.base.Preconditions;

/**
 * This convert will treat arg long value as a time offset from epoch and format it to time-date
 * format using current locale
 *
 * @author skarpushin
 */
public class DateTimeMessageArgConverter extends MessageArgConverter {
  public static final DateTimeMessageArgConverter INSTANCE = new DateTimeMessageArgConverter();

  /** Prevent from instantiating this class and enforce to use same instance everytime */
  private DateTimeMessageArgConverter() {}

  @Override
  public String convert(
      Object arg, HasMessageCode hasMessageCode, MessageSource messageSource, Locale locale) {
    Preconditions.checkArgument(arg != null);
    Preconditions.checkArgument(arg instanceof Long);
    DateFormat dateFormat =
        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
    return dateFormat.format(new Date((Long) arg));
  }
}
