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
package org.summerb.utils.spring;

import java.util.Locale;
import java.util.Properties;

import org.springframework.context.support.HackToGetCacheSeconds;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * This impl created in order to be able to read ALL messages available for application. So it will
 * be made available for client-side code
 *
 * @author sergeyk
 */
public class MessageSourceExposableImpl extends ReloadableResourceBundleMessageSource
    implements AllMessagesProvider {
  @Override
  public Properties getAllMessages(Locale locale) {
    clearCacheIncludingAncestors();
    PropertiesHolder propertiesHolder = getMergedProperties(locale);
    Properties properties = propertiesHolder.getProperties();
    return properties;
  }

  @Override
  public long getReloadIntervalSeconds() {
    return HackToGetCacheSeconds.getFrom(this);
  }
}
