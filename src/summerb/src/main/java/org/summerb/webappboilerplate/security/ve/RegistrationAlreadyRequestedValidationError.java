package org.summerb.webappboilerplate.security.ve;

import org.summerb.approaches.spring.security.SecurityMessageCodes;
import org.summerb.approaches.validation.ValidationError;
import org.summerb.microservices.users.api.dto.User;

public class RegistrationAlreadyRequestedValidationError extends ValidationError {
	private static final long serialVersionUID = -2574714397962614793L;

	@SuppressWarnings("deprecation")
	public RegistrationAlreadyRequestedValidationError() {
		super(SecurityMessageCodes.REGISTRATION_ALREADY_STARTED, User.FN_EMAIL);
	}
}
