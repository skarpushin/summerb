package org.summerb.utils.stringtemplate.api.validation;

import org.summerb.approaches.validation.ValidationError;

public class StringTemplateValidationError extends ValidationError {
	private static final long serialVersionUID = -7110646530687463433L;

	public static final String VALIDATION_MESSAGE_CODE_COMPILATION_ERROR = "validation.stringtemplate.compilationError";

	private Throwable cause;

	/**
	 * @deprecated only for io
	 */
	@Deprecated
	public StringTemplateValidationError() {
	}

	@SuppressWarnings("deprecation")
	public StringTemplateValidationError(String fieldToken, Throwable cause) {
		super(VALIDATION_MESSAGE_CODE_COMPILATION_ERROR, fieldToken);
		this.cause = cause;

		// TBD: Probably add more sophisticated analysis of error, to be able
		// to provide user with more details
	}

	public Throwable getCause() {
		return cause;
	}

}
