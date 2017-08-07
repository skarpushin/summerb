package org.springframework.context.support;

import java.text.MessageFormat;
import java.util.Locale;

public class HackToGetCacheSeconds extends AbstractResourceBasedMessageSource {
	public static long getFrom(AbstractResourceBasedMessageSource other) {
		return other.getCacheMillis() / 1000;
	}

	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		throw new IllegalStateException("This is not a real impl");
	}
}
