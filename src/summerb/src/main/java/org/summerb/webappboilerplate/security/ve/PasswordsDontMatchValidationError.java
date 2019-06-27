package org.summerb.webappboilerplate.security.ve;

import org.summerb.approaches.spring.security.SecurityMessageCodes;
import org.summerb.approaches.validation.ValidationError;
import org.summerb.webappboilerplate.security.dto.PasswordReset;

public class PasswordsDontMatchValidationError extends ValidationError {
	private static final long serialVersionUID = 6392441057143489663L;

	@SuppressWarnings("deprecation")
	public PasswordsDontMatchValidationError() {
		super(SecurityMessageCodes.VALIDATION_PASSWORDS_DO_NOT_MATCH, PasswordReset.FN_NEW_PASSWORD_AGAIN);
	}
}
