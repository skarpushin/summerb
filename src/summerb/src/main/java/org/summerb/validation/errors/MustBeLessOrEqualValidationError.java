package org.summerb.validation.errors;

import org.summerb.validation.ValidationError;

public class MustBeLessOrEqualValidationError extends ValidationError {
	private static final long serialVersionUID = -1520235304162692765L;

	private long subject;
	private long border;

	/**
	 * @deprecated used only for serialization
	 */
	@Deprecated
	public MustBeLessOrEqualValidationError() {
		super();
	}

	@SuppressWarnings("deprecation")
	public MustBeLessOrEqualValidationError(long subject, long border, String fieldToken) {
		super("validation.mustBeLessOrEq", fieldToken, subject, border);

		this.subject = subject;
		this.border = border;
	}

	public long getSubject() {
		return subject;
	}

	public long getBorder() {
		return border;
	}
}
