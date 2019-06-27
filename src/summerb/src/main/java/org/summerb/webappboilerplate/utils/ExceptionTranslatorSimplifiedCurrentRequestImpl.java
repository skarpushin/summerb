package org.summerb.webappboilerplate.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.utils.exceptions.translator.ExceptionTranslator;
import org.summerb.utils.exceptions.translator.ExceptionTranslatorSimplified;

public class ExceptionTranslatorSimplifiedCurrentRequestImpl implements ExceptionTranslatorSimplified {
	@Autowired
	private ExceptionTranslator exceptionTranslator;

	@Override
	public String buildUserMessage(Throwable t) {
		return exceptionTranslator.buildUserMessage(t, CurrentRequestUtils.getLocale());
	}
}
