package org.summerb.webappboilerplate.security.ve;

import org.summerb.approaches.spring.security.SecurityMessageCodes;
import org.summerb.approaches.validation.ValidationError;
import org.summerb.microservices.users.api.dto.User;

public class RegistrationActivationRequiredValidationError extends ValidationError {
	private static final long serialVersionUID = 4448690696428396195L;

	@SuppressWarnings("deprecation")
	public RegistrationActivationRequiredValidationError() {
		super(SecurityMessageCodes.CANT_LOGIN_UNTIL_REGISTRATION_ACTIVATED, User.FN_EMAIL);
	}
}
