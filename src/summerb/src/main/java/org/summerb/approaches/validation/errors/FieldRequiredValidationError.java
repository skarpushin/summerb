package org.summerb.approaches.validation.errors;

import org.summerb.approaches.validation.ValidationError;

public class FieldRequiredValidationError extends ValidationError {
	private static final long serialVersionUID = -5236960322508069758L;

	public static final String VALIDATION_MESSAGE_CODE_REQUIRED = "validation.field.required";

	/**
	 * @deprecated used only for serialization
	 */
	@Deprecated
	public FieldRequiredValidationError() {
		super();
	}

	@SuppressWarnings("deprecation")
	public FieldRequiredValidationError(String fieldToken) {
		super(VALIDATION_MESSAGE_CODE_REQUIRED, fieldToken);
	}
}
