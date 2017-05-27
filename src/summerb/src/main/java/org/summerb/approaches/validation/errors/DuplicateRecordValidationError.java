package org.summerb.approaches.validation.errors;

import org.summerb.approaches.validation.ValidationError;

public class DuplicateRecordValidationError extends ValidationError {
	private static final long serialVersionUID = 9024133364770713306L;

	/**
	 * @deprecated used only for serialization
	 */
	@Deprecated
	public DuplicateRecordValidationError() {
		super();
	}

	@SuppressWarnings("deprecation")
	public DuplicateRecordValidationError(String fieldToken) {
		super("validation.duplicateRecord", fieldToken);
	}
}
