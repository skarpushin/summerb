package org.summerb.utils.exceptions.translator;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.summerb.approaches.i18n.I18nUtils;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.approaches.validation.ValidationError;

public class ExceptionTranslatorFveImpl implements ExceptionTranslator {
	private MessageSource messageSource;

	@Autowired
	public ExceptionTranslatorFveImpl(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String buildUserMessage(Throwable t, Locale locale) {
		if (!FieldValidationException.class.equals(t.getClass())) {
			return null;
		}
		FieldValidationException fve = (FieldValidationException) t;

		StringBuilder ret = new StringBuilder();
		ret.append(I18nUtils.buildMessage(fve, messageSource, locale));
		ret.append(": ");
		boolean first = true;
		for (ValidationError ve : fve.getErrors()) {
			if (!first) {
				ret.append(", ");
			}
			ret.append(translateFieldName(ve.getFieldToken(), messageSource, locale));
			ret.append(" - ");
			ret.append(I18nUtils.buildMessage(ve, messageSource, locale));
			first = false;
		}
		return ret.toString();
	}

	private static Object translateFieldName(String fieldToken, MessageSource messageSource, Locale locale) {
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
}
