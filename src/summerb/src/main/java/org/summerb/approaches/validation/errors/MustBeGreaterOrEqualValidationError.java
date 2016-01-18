package org.summerb.approaches.validation.errors;

import org.summerb.approaches.validation.ValidationError;

public class MustBeGreaterOrEqualValidationError extends ValidationError {
	private static final long serialVersionUID = -1011940103095989459L;

	/**
	 * @deprecated used only for serialization
	 */
	@Deprecated
	public MustBeGreaterOrEqualValidationError() {
		super();
	}

	@SuppressWarnings("deprecation")
	public MustBeGreaterOrEqualValidationError(long subject, long border, String fieldToken) {
		super("validation.mustBeGreaterOrEq", fieldToken, subject, border);
	}

	@SuppressWarnings("deprecation")
	public MustBeGreaterOrEqualValidationError(double subject, double border, String fieldToken) {
		super("validation.mustBeGreaterOrEq", fieldToken, subject, border);
	}
}
