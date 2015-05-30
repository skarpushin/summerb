package org.summerb.spring;

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
 *
 */
public class ExposePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
	private Map<String, String> properties;

	@SuppressWarnings("rawtypes")
	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
			throws BeansException {
		Map<String, String> tmpProperties = new HashMap<String, String>(props.size());
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