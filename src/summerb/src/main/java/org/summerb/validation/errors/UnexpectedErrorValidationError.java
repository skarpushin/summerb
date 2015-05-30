package org.summerb.validation.errors;

import org.summerb.validation.ValidationError;

public class UnexpectedErrorValidationError extends ValidationError {
	private static final long serialVersionUID = -6386726326921207412L;

	@SuppressWarnings("deprecation")
	public UnexpectedErrorValidationError(String fieldToken, String message) {
		super("validation.exceptionUnexpected", fieldToken, message);
	}
}
