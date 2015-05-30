package org.summerb.validation.errors;

import org.summerb.validation.ValidationError;

public class NotANumberValidationError extends ValidationError {
	private static final long serialVersionUID = 1062293997316162723L;

	/**
	 * @deprecated used only for serialization
	 */
	@Deprecated
	public NotANumberValidationError() {
		super();
	}

	@SuppressWarnings("deprecation")
	public NotANumberValidationError(String fieldToken) {
		super("validation.notANumber", fieldToken);
	}

}
