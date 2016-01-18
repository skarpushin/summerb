package org.summerb.approaches.validation.errors;

import org.summerb.approaches.validation.ValidationError;

public class MustBeGreaterValidationError extends ValidationError {
	private static final long serialVersionUID = -1921913462418603691L;

	/**
	 * @deprecated used only for serialization
	 */
	@Deprecated
	public MustBeGreaterValidationError() {
		super();
	}

	@SuppressWarnings("deprecation")
	public MustBeGreaterValidationError(long subject, long border, String fieldToken) {
		super("validation.fieldMustBeGreater", fieldToken, subject, border);
	}

	@SuppressWarnings("deprecation")
	public MustBeGreaterValidationError(double subject, double border, String fieldToken) {
		super("validation.fieldMustBeGreater", fieldToken, subject, border);
	}

}
