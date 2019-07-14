package org.summerb.approaches.i18n;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

// TBD: Consider implementing customizable (based on strategies) regular bean instead of this singleton impl with fixed behavior

public abstract class I18nUtils {
	public static String buildMessage(HasMessageCode hasMessageCode, MessageSource messageSource, Locale locale) {
		try {
			Object[] args = null;
			if (hasMessageCode instanceof HasMessageArgs) {
				args = ((HasMessageArgs) hasMessageCode).getMessageArgs();
			}

			MessageArgConverter[] argsConverters = null;
			if (hasMessageCode instanceof HasMessageArgsConverters) {
				argsConverters = ((HasMessageArgsConverters) hasMessageCode).getMessageArgsConverters();
			}

			if (args != null && argsConverters != null) {
				applyArgsConversion(hasMessageCode, args, argsConverters, messageSource, locale);
			}

			return getMessage(hasMessageCode.getMessageCode(), args, messageSource, locale);
		} catch (Throwable t) {
			throw new RuntimeException("Failed to build message", t);
		}
	}

	protected static void applyArgsConversion(HasMessageCode hasMessageCode, Object[] args,
			MessageArgConverter[] argsConverters, MessageSource messageSource, Locale locale) {
		for (int i = 0; i < args.length; i++) {
			if (argsConverters.length < i + 1) {
				// there is no more converters, nothing to convert
				break;
			}
			if (argsConverters[i] == null) {
				continue;
			}

			args[i] = args[i] == null ? "(null)"
					: argsConverters[i].convert(args[i], hasMessageCode, messageSource, locale);
		}
	}

	protected static String getMessage(String messageCode, Object[] args, MessageSource messageSource, Locale locale) {
		try {
			return messageSource.getMessage(messageCode, args, locale);
		} catch (NoSuchMessageException nsme) {
			// as a backup plan just return message code
			return messageCode;
		}
	}

}
