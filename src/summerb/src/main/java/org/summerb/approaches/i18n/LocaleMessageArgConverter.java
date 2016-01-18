package org.summerb.approaches.i18n;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.google.common.base.Preconditions;

/**
 * This convert will convert Locale argument to it's string representation
 * suitable for presentation to user
 * 
 * @author skarpushin
 * 
 */
public class LocaleMessageArgConverter extends MessageArgConverter {
	public static final LocaleMessageArgConverter INSTANCE = new LocaleMessageArgConverter();

	/**
	 * Prevent from instantiating this class and enforce to use same instance
	 * everytime
	 */
	private LocaleMessageArgConverter() {
	}

	@Override
	public String convert(Object arg, HasMessageCode hasMessageCode, MessageSource messageSource) {
		Preconditions.checkArgument(arg != null);
		Preconditions.checkArgument(arg instanceof Locale);

		Locale locale = LocaleContextHolder.getLocale();
		return ((Locale) arg).getDisplayName(locale);
	}

}
