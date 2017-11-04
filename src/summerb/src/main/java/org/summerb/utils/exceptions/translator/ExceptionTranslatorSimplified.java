package org.summerb.utils.exceptions.translator;

/**
 * This interface is similar to {@link ExceptionTranslator} with only exception
 * - impl supposed to know how to resolve locale
 * 
 * @author sergeyk
 *
 */
public interface ExceptionTranslatorSimplified {
	/**
	 * Translate exception into user locale using provided messageSource
	 * 
	 * @return message ready for user OR null if this translator doesn't support
	 *         this type of exception
	 */
	String buildUserMessage(Throwable t);
}
