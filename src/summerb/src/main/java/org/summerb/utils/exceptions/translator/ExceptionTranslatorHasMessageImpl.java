package org.summerb.utils.exceptions.translator;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.summerb.approaches.i18n.HasMessageCode;
import org.summerb.approaches.i18n.I18nUtils;

public class ExceptionTranslatorHasMessageImpl implements ExceptionTranslator {
	private MessageSource messageSource;

	@Autowired
	public ExceptionTranslatorHasMessageImpl(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String buildUserMessage(Throwable t, Locale locale) {
		if (!HasMessageCode.class.isAssignableFrom(t.getClass())) {
			return null;
		}
		HasMessageCode hasMessage = (HasMessageCode) t;
		return I18nUtils.buildMessage(hasMessage, messageSource, locale);
	}
}
