package org.summerb.approaches.springmvc.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.summerb.approaches.i18n.HasMessageCode;
import org.summerb.approaches.i18n.I18nUtils;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.approaches.validation.ValidationError;

public class ErrorUtils {
	public static String getAllMessages(Throwable t) {
		if (t == null)
			return "";

		StringBuilder ret = new StringBuilder();

		MessageSource messageSource = CurrentRequestUtils.getWac();
		Locale locale = LocaleContextHolder.getLocale();

		Throwable cur = t;
		while (cur != null) {
			if (cur == cur.getCause())
				break;

			if (ret.length() > 0) {
				ret.append(" -> ");
			}

			if (cur instanceof FieldValidationException) {
				FieldValidationException fve = (FieldValidationException) cur;
				ret.append(buildMessageForFve(fve, messageSource, locale));
			} else if (cur instanceof HasMessageCode) {
				ret.append(buildMessageFromMessageCode((HasMessageCode) cur));
			} else {
				ret.append(tryBuildMessageBasedOnExcClassName(cur, messageSource, locale));
			}

			cur = cur.getCause();
		}

		return ret.toString();
	}

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

	protected static String buildMessageFromMessageCode(HasMessageCode cur) {
		return I18nUtils.buildMessage(cur, CurrentRequestUtils.getWac());
	}

	protected static StringBuilder buildMessageForFve(FieldValidationException fve, MessageSource messageSource,
			Locale locale) {
		StringBuilder ret = new StringBuilder();
		ret.append(I18nUtils.buildMessage(fve, messageSource));
		ret.append(": ");
		boolean first = true;
		for (ValidationError ve : fve.getErrors()) {
			if (!first) {
				ret.append(", ");
			}
			ret.append(tryFindTranslation(ve.getFieldToken(), messageSource, locale));
			ret.append(" - ");
			ret.append(I18nUtils.buildMessage(ve, messageSource));
			first = false;
		}
		return ret;
	}

	private static Object tryFindTranslation(String fieldToken, MessageSource messageSource, Locale locale) {
		try {
			return messageSource.getMessage(fieldToken, null, locale);
		} catch (NoSuchMessageException nfe) {
			try {
				return messageSource.getMessage("term." + fieldToken, null, locale);
			} catch (NoSuchMessageException nfe2) {
				return fieldToken;
			}
		}
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
					buildMessage(messageSource, locale, cur)));

			cur = cur.getCause();
		}

		return ret.toArray(new ExceptionDescription[0]);
	}

	protected static String buildMessage(MessageSource messageSource, Locale locale, Throwable cur) {
		String message;
		if (cur instanceof FieldValidationException) {
			FieldValidationException fve = (FieldValidationException) cur;
			message = buildMessageForFve(fve, messageSource, locale).toString();
		} else if (cur instanceof HasMessageCode) {
			message = buildMessageFromMessageCode((HasMessageCode) cur);
		} else {
			message = tryBuildMessageBasedOnExcClassName(cur, messageSource, locale);
		}
		return message;
	}
}
