package org.summerb.approaches.springmvc.security.ve;

import org.summerb.approaches.springmvc.security.SecurityMessageCodes;
import org.summerb.approaches.validation.ValidationError;
import org.summerb.microservices.users.api.dto.User;

public class RegistrationRequiredValidationError extends ValidationError {
	private static final long serialVersionUID = 4448690696428396195L;

	@SuppressWarnings("deprecation")
	public RegistrationRequiredValidationError() {
		super(SecurityMessageCodes.ACCOUNT_IS_NOT_CREATED_PLEASE_REGISTER, User.FN_EMAIL);
	}
}
