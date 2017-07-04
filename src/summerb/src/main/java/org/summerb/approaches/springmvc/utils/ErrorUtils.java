package org.summerb.approaches.springmvc.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.summerb.utils.exceptions.translator.ExceptionTranslator;
import org.summerb.utils.exceptions.translator.ExceptionTranslatorLegacyImpl;

/**
 * @deprecated Use ExceptionTranslator infrastructure instead.
 * @author sergeyk
 *
 */
@Deprecated
public class ErrorUtils {
	protected static final Logger log = Logger.getLogger(ErrorUtils.class);
	private static ExceptionTranslator exceptionTranslator = new ExceptionTranslatorLegacyImpl();
	private static List<ExceptionTranslator> translators = ExceptionTranslatorLegacyImpl.buildLegacyTranslatorsList();

	public static String getAllMessages(Throwable t) {
		return exceptionTranslator.buildUserMessage(t, CurrentRequestUtils.getWac(), LocaleContextHolder.getLocale());
	}

	public static ExceptionDescription[] getExceptionDescriptions(Exception t) {
		if (t == null) {
			return new ExceptionDescription[0];
		}

		List<ExceptionDescription> ret = new LinkedList<ExceptionDescription>();

		MessageSource messageSource = CurrentRequestUtils.getWac();
		Locale locale = LocaleContextHolder.getLocale();

		Throwable cur = t;
		while (cur != null) {
			if (cur == cur.getCause())
				break;

			ret.add(new ExceptionDescription(cur.getClass().getCanonicalName(),
					buildMessage(cur, messageSource, locale)));

			cur = cur.getCause();
		}

		return ret.toArray(new ExceptionDescription[0]);
	}

	private static String buildMessage(Throwable cur, MessageSource messageSource, Locale locale) {
		for (ExceptionTranslator translator : translators) {
			String msg = translator.buildUserMessage(cur, messageSource, locale);
			if (msg != null) {
				return msg;
			}
		}
		return "";
	}

}
