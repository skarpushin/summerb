package org.summerb.utils.exceptions.translator;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.summerb.approaches.i18n.HasMessageCode;
import org.summerb.approaches.i18n.I18nUtils;

public class ExceptionTranslatorHasMessageImpl implements ExceptionTranslator {
	@Override
	public String buildUserMessage(Throwable t, MessageSource messageSource, Locale locale) {
		if (!HasMessageCode.class.isAssignableFrom(t.getClass())) {
			return null;
		}
		HasMessageCode hasMessage = (HasMessageCode) t;
		return I18nUtils.buildMessage(hasMessage, messageSource, locale);
	}
}
