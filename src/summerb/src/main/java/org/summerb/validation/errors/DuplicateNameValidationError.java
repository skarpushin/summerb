package org.summerb.validation.errors;

import org.summerb.validation.ValidationError;

public class DuplicateNameValidationError extends ValidationError {
	private static final long serialVersionUID = -537217996301287218L;

	/**
	 * @deprecated used only for serialization
	 */
	@Deprecated
	public DuplicateNameValidationError() {
		super();
	}

	@SuppressWarnings("deprecation")
	public DuplicateNameValidationError(String fieldToken) {
		super("validation.duplicateName", fieldToken);
	}
}
