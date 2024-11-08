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
package org.summerb.utils.spring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * That is used to make following work:
 *
 * <pre>
 * &#064;Autowired
 * &#064;Value(&quot;#{ props.properties['profile.dev'] }&quot;)
 * private boolean isDevMode;
 * </pre>
 *
 * @author sergey.karpushin
 */
@SuppressWarnings("deprecation")
public class ExposePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
  private Map<String, String> properties;

  @SuppressWarnings("rawtypes")
  @Override
  protected void processProperties(
      ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
      throws BeansException {
    Map<String, String> tmpProperties = new HashMap<>(props.size());
    super.processProperties(beanFactoryToProcess, props);
    for (Entry entry : props.entrySet()) {
      tmpProperties.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
    }
    this.properties = Collections.unmodifiableMap(tmpProperties);
  }

  public Map<String, String> getProperties() {
    return this.properties;
  }
}
