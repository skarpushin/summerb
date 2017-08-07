package org.summerb.approaches.springmvc.security.ve;

import org.summerb.approaches.springmvc.security.SecurityMessageCodes;
import org.summerb.approaches.springmvc.security.dto.PasswordResetPm;
import org.summerb.approaches.validation.ValidationError;

public class PasswordsDontMatchValidationError extends ValidationError {
	private static final long serialVersionUID = 6392441057143489663L;

	@SuppressWarnings("deprecation")
	public PasswordsDontMatchValidationError() {
		super(SecurityMessageCodes.VALIDATION_PASSWORDS_DO_NOT_MATCH, PasswordResetPm.FN_NEW_PASSWORD_AGAIN);
	}
}
