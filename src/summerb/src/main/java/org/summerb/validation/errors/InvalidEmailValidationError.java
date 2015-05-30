package org.summerb.validation.errors;

import org.summerb.validation.ValidationError;

public class InvalidEmailValidationError extends ValidationError {
	private static final long serialVersionUID = -8119415446133607944L;

	public static final String VALIDATION_MESSAGE_CODE_INVALID_FORMAT_EMAIL = "validation.invalid.email.format";

	/**
	 * @deprecated used only for serialization
	 */
	@Deprecated
	public InvalidEmailValidationError() {
		super();
	}

	@SuppressWarnings("deprecation")
	public InvalidEmailValidationError(String fieldToken) {
		super(VALIDATION_MESSAGE_CODE_INVALID_FORMAT_EMAIL, fieldToken);
	}
}
