package org.summerb.approaches.validation.errors;

import org.summerb.approaches.validation.ValidationError;

public class TooShortStringValidationError extends ValidationError {
	private static final long serialVersionUID = -1921913462418603691L;

	/**
	 * @deprecated used only for serialization
	 */
	@Deprecated
	public TooShortStringValidationError() {
		super();
	}

	@SuppressWarnings("deprecation")
	public TooShortStringValidationError(long border, String fieldToken) {
		super("validation.stringTooShort", fieldToken, border);
	}

}
