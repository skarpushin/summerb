package org.summerb.utils.exceptions.translator;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

public class ExceptionTranslatorLegacyImpl extends ExceptionTranslatorDelegatingImpl {
	@Autowired
	public ExceptionTranslatorLegacyImpl(MessageSource messageSource) {
		super(buildLegacyTranslatorsList(messageSource));
	}

	public static List<ExceptionTranslator> buildLegacyTranslatorsList(MessageSource messageSource) {
		return Arrays.asList((ExceptionTranslator) new ExceptionTranslatorFveImpl(messageSource),
				(ExceptionTranslator) new ExceptionTranslatorHasMessageImpl(messageSource),
				(ExceptionTranslator) new ExceptionTranslatorClassNameImpl(messageSource));
	}
}
