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

import com.google.common.base.Preconditions;
import java.util.Locale;
import org.springframework.context.MessageSource;

/**
 * This convert will convert Locale argument to it's string representation suitable for presentation
 * to user
 *
 * @author skarpushin
 */
public class LocaleMessageArgConverter extends MessageArgConverter {
  public static final LocaleMessageArgConverter INSTANCE = new LocaleMessageArgConverter();

  /** Prevent from instantiating this class and enforce to use same instance everytime */
  private LocaleMessageArgConverter() {}

  @Override
  public String convert(
      Object arg, HasMessageCode hasMessageCode, MessageSource messageSource, Locale locale) {
    Preconditions.checkArgument(arg != null);
    Preconditions.checkArgument(arg instanceof Locale);
    return ((Locale) arg).getDisplayName(locale);
  }
}
