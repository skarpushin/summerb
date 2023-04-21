package org.summerb.easycrud.api;

import org.summerb.validation.FieldValidationException;

public abstract class DaoExceptionTranslatorAbstract implements DaoExceptionTranslator {

	@Override
	public void translateAndThrowIfApplicableUnchecked(Exception e) {
		try {
			translateAndThrowIfApplicable(e);
		} catch (FieldValidationException e1) {
			throw new RuntimeException("FieldValidationException", e1);
		}
	}

}
