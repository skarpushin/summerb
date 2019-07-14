package org.summerb.approaches.i18n;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.google.common.base.Preconditions;

/**
 * This convert will treat arg long value as a time offset from epoch and format
 * it to time-date format using corrent locale
 * 
 * @author skarpushin
 * 
 */
public class DateTimeMessageArgConverter extends MessageArgConverter {
	public static final DateTimeMessageArgConverter INSTANCE = new DateTimeMessageArgConverter();

	/**
	 * Prevent from instantiating this class and enforce to use same instance
	 * everytime
	 */
	private DateTimeMessageArgConverter() {
	}

	@Override
	public String convert(Object arg, HasMessageCode hasMessageCode, MessageSource messageSource, Locale locale) {
		Preconditions.checkArgument(arg != null);
		Preconditions.checkArgument(arg instanceof Long);
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
		return dateFormat.format(new Date((Long) arg));
	}
}
