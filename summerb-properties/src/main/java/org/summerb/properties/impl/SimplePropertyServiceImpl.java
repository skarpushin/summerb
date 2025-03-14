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
package org.summerb.properties.impl;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.summerb.properties.api.PropertyService;
import org.summerb.properties.api.SimplePropertyService;
import org.summerb.properties.api.dto.NamedProperty;
import org.summerb.properties.api.dto.SimplePropertiesSubject;
import org.summerb.utils.easycrud.api.dto.EntityChangedEvent;

public class SimplePropertyServiceImpl implements SimplePropertyService, InitializingBean {
  protected PropertyService propertyService;
  protected String appName;
  protected String domainName;

  protected EventBus eventBus;

  public SimplePropertyServiceImpl(
      PropertyService propertyService, String appName, String domainName) {
    this.propertyService = propertyService;
    this.appName = appName;
    this.domainName = domainName;
  }

  @Override
  public void afterPropertiesSet() {
    Preconditions.checkArgument(propertyService != null);
    Preconditions.checkArgument(StringUtils.hasText(appName));
    Preconditions.checkArgument(StringUtils.hasText(domainName));
  }

  @Override
  public Map<String, String> findSubjectProperties(String subjectId) {
    return propertyService.findSubjectProperties(appName, domainName, subjectId);
  }

  @Override
  public String findSubjectProperty(String subjectId, String propertyName) {
    return propertyService.findSubjectProperty(appName, domainName, subjectId, propertyName);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void putSubjectProperties(String subjectId, List<NamedProperty> namedProperties) {
    propertyService.putSubjectProperties(appName, domainName, subjectId, namedProperties);
    fireSubjectPropertiesChanged(subjectId);
  }

  protected void fireSubjectPropertiesChanged(String subjectId) {
    if (eventBus != null) {
      eventBus.post(
          EntityChangedEvent.updated(new SimplePropertiesSubject(appName, domainName, subjectId)));
    }
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void putSubjectProperty(String subjectId, String propertyName, String propertyValue) {
    propertyService.putSubjectProperty(appName, domainName, subjectId, propertyName, propertyValue);
    fireSubjectPropertiesChanged(subjectId);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void deleteSubjectProperties(String subjectId) {
    propertyService.deleteSubjectProperties(appName, domainName, subjectId);
    fireSubjectPropertiesChanged(subjectId);
  }

  public PropertyService getPropertyService() {
    return propertyService;
  }

  @Override
  public String getAppName() {
    return appName;
  }

  @Override
  public String getDomainName() {
    return domainName;
  }

  public EventBus getEventBus() {
    return eventBus;
  }

  @Autowired(required = false)
  public void setEventBus(EventBus eventBus) {
    this.eventBus = eventBus;
  }
}
