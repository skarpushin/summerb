package org.summerb.utils.exceptions.translator;

import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.approaches.springmvc.utils.CurrentRequestUtils;

public class ExceptionTranslatorSimplifiedCurrentRequestImpl implements ExceptionTranslatorSimplified {
	@Autowired
	private ExceptionTranslator exceptionTranslator;

	@Override
	public String buildUserMessage(Throwable t) {
		return exceptionTranslator.buildUserMessage(t, CurrentRequestUtils.getLocale());
	}
}
