package org.summerb.utils.exceptions.translator;

import java.util.Locale;

import org.springframework.context.MessageSource;

public interface ExceptionTranslator {
	/**
	 * Translate exception into user locale using provided messageSource
	 * 
	 * @return message ready for user OR null if this translator doesn't support
	 *         this type of exception
	 */
	String buildUserMessage(Throwable t, MessageSource messageSource, Locale locale);
}
