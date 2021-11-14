/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudServiceResolver;

import com.google.common.base.Preconditions;

/**
 * That impl just finds all beans with {@link EasyCrudService} interface
 * implemented
 * 
 * @author sergeyk
 *
 */
@SuppressWarnings("rawtypes")
public class EasyCrudServiceResolverSpringImpl implements EasyCrudServiceResolver, ApplicationContextAware {
	private Logger log = LogManager.getLogger(getClass());

	private ApplicationContext applicationContext;
	private Map<String, EasyCrudService> servicesMap;
	private Map<Class<?>, EasyCrudService> servicesMapByClass;

	@Override
	public EasyCrudService resolveByEntityType(String entityName) {
		EasyCrudService ret = getServicesMap().get(entityName);
		Preconditions.checkArgument(ret != null, "Serivce for that entity wasn't found: " + entityName);
		return ret;
	}

	@Override
	public EasyCrudService resolveByDtoClass(Class<?> entityClass) {
		getServicesMap();
		EasyCrudService ret = servicesMapByClass.get(entityClass);
		Preconditions.checkArgument(ret != null, "Serivce for that entity (by class) wasn't found: " + entityClass);
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

	private Map<String, EasyCrudService> discoverServices() {
		Preconditions.checkState(applicationContext != null, "applicationContext expected to be injected");
		Map<String, EasyCrudService> foundBeans = applicationContext.getBeansOfType(EasyCrudService.class);
		Map<String, EasyCrudService> ret = new HashMap<>();
		for (Entry<String, EasyCrudService> entry : foundBeans.entrySet()) {
			EasyCrudService service = entry.getValue();
			EasyCrudService wasOverwritten = ret.put(service.getEntityTypeMessageCode(), service);
			if (wasOverwritten != null) {
				log.warn("Ambigious EasyCrudService for same entityTypeMessageCode 1st " + wasOverwritten + " and 2nd "
						+ service + " named " + entry.getKey());
			}
		}
		return ret;
	}

	private Map<Class<?>, EasyCrudService> discoverServicesByClass() {
		Preconditions.checkState(applicationContext != null, "applicationContext expected to be injected");
		Map<String, EasyCrudService> foundBeans = applicationContext.getBeansOfType(EasyCrudService.class);
		Map<Class<?>, EasyCrudService> ret = new HashMap<>();
		for (Entry<String, EasyCrudService> entry : foundBeans.entrySet()) {
			EasyCrudService service = entry.getValue();
			EasyCrudService wasOverwritten = ret.put(service.getDtoClass(), service);
			if (wasOverwritten != null) {
				log.warn("Ambigious EasyCrudService for same dtoClass 1st " + wasOverwritten + " and 2nd " + service
						+ " named " + entry.getKey());
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
