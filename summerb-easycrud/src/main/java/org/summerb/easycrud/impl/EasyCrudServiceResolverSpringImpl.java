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
package org.summerb.easycrud.impl;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudServiceResolver;

/**
 * That impl just finds all beans with {@link EasyCrudService} interface implemented
 *
 * @author sergeyk
 */
@SuppressWarnings("rawtypes")
public class EasyCrudServiceResolverSpringImpl
    implements EasyCrudServiceResolver, ApplicationContextAware {
  protected Logger log = LoggerFactory.getLogger(getClass());

  protected ApplicationContext applicationContext;
  protected volatile Map<String, EasyCrudService> servicesMap;
  protected Map<Class<?>, EasyCrudService> servicesMapByClass;

  @Override
  public EasyCrudService resolveByRowMessageCode(String entityName) {
    EasyCrudService ret = getServicesMap().get(entityName);
    Preconditions.checkArgument(ret != null, "Serivce for that entity wasn't found: " + entityName);
    return ret;
  }

  @Override
  public EasyCrudService resolveByRowClass(Class<?> entityClass) {
    getServicesMap();
    EasyCrudService ret = servicesMapByClass.get(entityClass);
    Preconditions.checkArgument(
        ret != null, "Serivce for that entity (by class) wasn't found: " + entityClass);
    return ret;
  }

  public Map<String, EasyCrudService> getServicesMap() {
    if (servicesMap == null) {
      synchronized (this) {
        if (servicesMap == null) {
          servicesMap = discoverServices();
          servicesMapByClass = discoverServicesByClass();
        }
      }
    }
    return servicesMap;
  }

  protected Map<String, EasyCrudService> discoverServices() {
    Preconditions.checkState(
        applicationContext != null, "applicationContext expected to be injected");
    Map<String, EasyCrudService> foundBeans =
        applicationContext.getBeansOfType(EasyCrudService.class);
    Map<String, EasyCrudService> ret = new HashMap<>();
    for (Entry<String, EasyCrudService> entry : foundBeans.entrySet()) {
      EasyCrudService service = entry.getValue();
      EasyCrudService wasOverwritten = ret.put(service.getRowMessageCode(), service);
      if (wasOverwritten != null) {
        log.warn(
            "Ambigious EasyCrudService for same entityTypeMessageCode 1st {} and 2nd {} named {}",
            wasOverwritten,
            service,
            entry.getKey());
      }
    }
    return ret;
  }

  protected Map<Class<?>, EasyCrudService> discoverServicesByClass() {
    Preconditions.checkState(
        applicationContext != null, "applicationContext expected to be injected");
    Map<String, EasyCrudService> foundBeans =
        applicationContext.getBeansOfType(EasyCrudService.class);
    Map<Class<?>, EasyCrudService> ret = new HashMap<>();
    for (Entry<String, EasyCrudService> entry : foundBeans.entrySet()) {
      EasyCrudService service = entry.getValue();
      EasyCrudService wasOverwritten = ret.put(service.getRowClass(), service);
      if (wasOverwritten != null) {
        log.warn(
            "Ambigious EasyCrudService for same rowClass 1st {} and 2nd {} named {}",
            wasOverwritten,
            service,
            entry.getKey());
      }
    }
    return ret;
  }

  public void setServicesMap(Map<String, EasyCrudService> servicesMap) {
    this.servicesMap = servicesMap;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
