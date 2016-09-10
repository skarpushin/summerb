package org.summerb.approaches.jdbccrud.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.summerb.approaches.jdbccrud.api.EasyCrudService;
import org.summerb.approaches.jdbccrud.api.EasyCrudServiceResolver;

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
	private Logger log = Logger.getLogger(getClass());

	private ApplicationContext applicationContext;
	private Map<String, EasyCrudService> servicesMap;

	@Override
	public EasyCrudService resolveByEntityType(String entityName) {
		EasyCrudService ret = getServicesMap().get(entityName);
		Preconditions.checkArgument(ret != null, "Serivce for that entity wasn't found: " + entityName);
		return ret;
	}

	public Map<String, EasyCrudService> getServicesMap() {
		if (servicesMap == null) {
			synchronized (this) {
				if (servicesMap == null) {
					Map<String, EasyCrudService> discoveredMap = discoverServices();
					servicesMap = discoveredMap;
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

	public void setServicesMap(Map<String, EasyCrudService> servicesMap) {
		this.servicesMap = servicesMap;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
