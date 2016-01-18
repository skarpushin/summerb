package org.summerb.approaches.validation.errors;

import org.summerb.approaches.validation.ValidationError;

public class NumberOutOfRangeValidationError extends ValidationError {
	private static final long serialVersionUID = 1159149569870010322L;

	/**
	 * @deprecated used only for serialization
	 */
	@Deprecated
	public NumberOutOfRangeValidationError() {
		super();
	}

	@SuppressWarnings("deprecation")
	public NumberOutOfRangeValidationError(long subject, long lowerBorder, long upperBorder, String fieldToken) {
		super("validation.numberOutOfRange", fieldToken, subject, lowerBorder, upperBorder);
	}

}
