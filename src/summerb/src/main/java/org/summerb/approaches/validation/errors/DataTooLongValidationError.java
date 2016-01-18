package org.summerb.approaches.validation.errors;

import org.summerb.approaches.validation.ValidationError;

public class DataTooLongValidationError extends ValidationError {
	private static final long serialVersionUID = 7170965971828651975L;

	public static final String VALIDATION_MESSAGE_CODE_TOO_LONG = "validation.data.too.long";
	private int providedSize = -1;
	private int acceptableSize = -1;

	/**
	 * @deprecated used only for serialization
	 */
	@Deprecated
	public DataTooLongValidationError() {
		super();
	}

	@SuppressWarnings("deprecation")
	public DataTooLongValidationError(int dataSize, int acceptableSize, String fieldToken) {
		super(VALIDATION_MESSAGE_CODE_TOO_LONG, fieldToken, dataSize, acceptableSize);

		this.providedSize = dataSize;
		this.acceptableSize = acceptableSize;
	}

	public int getProvidedSize() {
		return providedSize;
	}

	public int getAcceptableSize() {
		return acceptableSize;
	}

}
