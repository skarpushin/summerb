package org.summerb.utils.spring;

import java.util.Locale;
import java.util.Properties;

import org.springframework.context.support.HackToGetCacheSeconds;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * This impl created in order to be able to read ALL messages available for
 * application. So it will be made available for client-side code
 * 
 * @author sergeyk
 *
 */
public class MessageSourceExposableImpl extends ReloadableResourceBundleMessageSource implements AllMessagesProvider {
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
