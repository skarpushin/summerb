package org.summerb.utils.exceptions.translator;

import java.util.Arrays;
import java.util.List;

public class ExceptionTranslatorLegacyImpl extends ExceptionTranslatorDelegatingImpl {
	public ExceptionTranslatorLegacyImpl() {
		super(buildLegacyTranslatorsList());
	}

	public static List<ExceptionTranslator> buildLegacyTranslatorsList() {
		return Arrays.asList((ExceptionTranslator) new ExceptionTranslatorFveImpl(),
				(ExceptionTranslator) new ExceptionTranslatorHasMessageImpl(),
				(ExceptionTranslator) new ExceptionTranslatorClassNameImpl());
	}
}
