package org.summerb.easycrud.api;

import org.summerb.validation.ValidationException;

public abstract class DaoExceptionTranslatorAbstract implements DaoExceptionTranslator {

	@Override
	public void translateAndThrowIfApplicableUnchecked(Exception e) {
		try {
			translateAndThrowIfApplicable(e);
		} catch (ValidationException e1) {
			throw new RuntimeException("ValidationException", e1);
		}
	}

}
