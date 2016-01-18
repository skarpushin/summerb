package org.summerb.microservices.users.api.validation;

import org.summerb.approaches.validation.ValidationError;

public class DuplicateUserValidationError extends ValidationError {
	private static final long serialVersionUID = -2231143102381068894L;

	public static final String VALIDATION_MESSAGE_CODE_DUPLICATE_USER = "duplicate.user";

	/**
	 * @deprecated used only for serialization
	 */
	@Deprecated
	public DuplicateUserValidationError() {
		super();
	}

	public DuplicateUserValidationError(String fieldToken) {
		super(VALIDATION_MESSAGE_CODE_DUPLICATE_USER, fieldToken);
	}

}
