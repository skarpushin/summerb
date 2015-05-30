package org.summerb.validation.errors;

import org.summerb.validation.ValidationError;

public class StringTooShortValidationError extends ValidationError {
	private static final long serialVersionUID = -1550410569217855201L;

	@SuppressWarnings("deprecation")
	public StringTooShortValidationError(String fieldToken, int minimumLength) {
		super("validation.string.too.short", fieldToken, minimumLength);
	}
}
