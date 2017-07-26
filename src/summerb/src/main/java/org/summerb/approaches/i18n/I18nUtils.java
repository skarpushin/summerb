package org.summerb.approaches.i18n;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

// TODO: Consider implementing customizable (based on strategies) regular bean instead of this singleton impl with fixed behavior

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
				applyArgsConversion(hasMessageCode, args, argsConverters, messageSource);
			}

			return getMessage(hasMessageCode.getMessageCode(), args, messageSource, locale);
		} catch (Throwable t) {
			throw new RuntimeException("Failed to build message", t);
		}
	}

	/**
	 * @deprecated use
	 *             {@link #buildMessage(HasMessageCode, MessageSource, Locale)}
	 *             instead
	 */
	@Deprecated
	public static String buildMessage(HasMessageCode hasMessageCode, MessageSource messageSource) {
		return buildMessage(hasMessageCode, messageSource, LocaleContextHolder.getLocale());
	}

	/**
	 * @deprecated Do not use this class for exceptions translation. Use
	 *             ExceptionTranslator infrastructure instead
	 */
	@Deprecated
	public static String buildMessagesChain(Throwable t, MessageSource messageSource) {
		if (t == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		Locale locale = LocaleContextHolder.getLocale();

		Throwable cur = t;
		while (cur != null) {
			if (sb.length() > 0) {
				sb.append(" -> ");
			}

			if (cur instanceof HasMessageCode) {
				sb.append(buildMessage((HasMessageCode) cur, messageSource));
			} else {
				sb.append(tryBuildMessageBasedOnExcClassName(cur, messageSource, locale));
			}

			if (cur == cur.getCause()) {
				break;
			}
			cur = cur.getCause();
		}

		return sb.toString();
	}

	private static void applyArgsConversion(HasMessageCode hasMessageCode, Object[] args,
			MessageArgConverter[] argsConverters, MessageSource messageSource) {
		for (int i = 0; i < args.length; i++) {
			if (argsConverters.length < i + 1) {
				// there is no more converters, nothing to convert
				break;
			}
			if (argsConverters[i] == null) {
				continue;
			}

			args[i] = args[i] == null ? "(null)" : argsConverters[i].convert(args[i], hasMessageCode, messageSource);
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

	/**
	 * @deprecated use
	 *             {@link #getMessage(String, Object[], MessageSource, Locale)}
	 *             instead
	 */
	@Deprecated
	protected static String getMessage(String messageCode, Object[] args, MessageSource messageSource) {
		return getMessage(messageCode, args, messageSource, LocaleContextHolder.getLocale());
	}

	/**
	 * @deprecated Do not use this class for exceptions translation. Use
	 *             ExceptionTranslator infrastructure instead
	 */
	@Deprecated
	protected static String tryBuildMessageBasedOnExcClassName(Throwable cur, MessageSource messageSource,
			Locale locale) {
		try {
			String className = cur.getClass().getName();
			String messageMappingForClassName = messageSource.getMessage(className, new Object[] { cur.getMessage() },
					locale);
			return messageMappingForClassName;
		} catch (NoSuchMessageException nfe) {
			return cur.getLocalizedMessage();
		}
	}

}
