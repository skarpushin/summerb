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
package org.summerb.properties.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.ObjectUtils;
import org.summerb.easycrud.api.dto.EntityChangedEvent;
import org.summerb.properties.api.SimplePropertyService;
import org.summerb.properties.api.dto.NamedProperty;
import org.summerb.properties.api.dto.SimplePropertiesSubject;
import org.summerb.utils.cache.CachesInvalidationNeeded;
import org.summerb.utils.tx.TransactionBoundCache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class SimplePropertyServiceCachedImpl implements SimplePropertyService, InitializingBean {
  protected SimplePropertyService simplePropertyService;
  protected EventBus eventBus;

  protected LoadingCache<String, Map<String, String>> cache;

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void afterPropertiesSet() throws Exception {
    String jmxName =
        "SimplePropertyServiceCachedImpl_"
            + simplePropertyService.getAppName()
            + "_"
            + simplePropertyService.getDomainName();
    CacheBuilder cacheBuilder = CacheBuilder.newBuilder().maximumSize(5000).recordStats();
    cache = new TransactionBoundCache<>(jmxName, cacheBuilder, loader);
    eventBus.register(this);
  }

  protected CacheLoader<String, Map<String, String>> loader =
      new CacheLoader<String, Map<String, String>>() {
        @Override
        public Map<String, String> load(String key) {
          Map<String, String> ret = simplePropertyService.findSubjectProperties(key);
          return ret == null ? new HashMap<String, String>() : ret;
        }
      };

  @Subscribe
  public void onEntityChange(EntityChangedEvent<SimplePropertiesSubject> evt) {
    if (!evt.isTypeOf(SimplePropertiesSubject.class)) {
      return;
    }

    if (!isSameDomain(evt.getValue())) {
      return;
    }

    String subjectId = evt.getValue().getSubjectId();
    cache.invalidate(subjectId);
  }

  protected boolean isSameDomain(SimplePropertiesSubject value) {
    return simplePropertyService.getAppName().equals(value.getAppName())
        && simplePropertyService.getDomainName().equals(value.getDomainName());
  }

  @Subscribe
  public void onCacheInvalidationRequest(CachesInvalidationNeeded evt) {
    cache.invalidateAll();
  }

  @Override
  public String findSubjectProperty(String subjectId, String propertyName) {
    return findSubjectProperties(subjectId).get(propertyName);
  }

  @Override
  public Map<String, String> findSubjectProperties(String subjectId) {
    return cache.getUnchecked(subjectId);
  }

  @Override
  public void putSubjectProperties(String subjectId, List<NamedProperty> namedProperties) {
    simplePropertyService.putSubjectProperties(subjectId, namedProperties);
  }

  @Override
  public void putSubjectProperty(String subjectId, String propertyName, String propertyValue) {
    Map<String, String> cachedProp = cache.getIfPresent(subjectId);
    if (cachedProp != null
        && ObjectUtils.nullSafeEquals(cachedProp.get(propertyName), propertyValue)) {
      // no change detected - so not changing
      return;
    }
    simplePropertyService.putSubjectProperty(subjectId, propertyName, propertyValue);
  }

  @Override
  public void deleteSubjectProperties(String subjectId) {
    simplePropertyService.deleteSubjectProperties(subjectId);
  }

  public SimplePropertyService getSimplePropertyService() {
    return simplePropertyService;
  }

  @Required
  public void setSimplePropertyService(SimplePropertyService service) {
    this.simplePropertyService = service;
  }

  @Override
  public String getAppName() {
    return simplePropertyService.getAppName();
  }

  @Override
  public String getDomainName() {
    return simplePropertyService.getDomainName();
  }

  public EventBus getEventBus() {
    return eventBus;
  }

  @Autowired
  public void setEventBus(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  public void resetCaches() {
    cache.invalidateAll();
  }
}
