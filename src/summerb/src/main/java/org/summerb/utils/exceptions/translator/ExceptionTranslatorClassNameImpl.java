package org.summerb.utils.exceptions.translator;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

/**
 * This translator treats class name as a message code and it's message as a
 * first argument message
 * 
 * @author sergeyk
 *
 */
public class ExceptionTranslatorClassNameImpl implements ExceptionTranslator {
	@Override
	public String buildUserMessage(Throwable t, MessageSource messageSource, Locale locale) {
		try {
			String className = t.getClass().getName();
			String messageMappingForClassName = messageSource.getMessage(className, new Object[] { t.getMessage() },
					locale);
			return messageMappingForClassName;
		} catch (NoSuchMessageException nfe) {
			return t.getClass().getSimpleName() + " (" + t.getLocalizedMessage() + ")";
		}
	}
}
