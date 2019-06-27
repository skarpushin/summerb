package org.summerb.webappboilerplate.security.ve;

import org.summerb.approaches.validation.ValidationError;
import org.summerb.microservices.users.api.exceptions.InvalidPasswordException;
import org.summerb.webappboilerplate.security.dto.LoginParams;

public class PasswordInvalidValidationError extends ValidationError {
	private static final long serialVersionUID = 5184851404690565907L;

	@SuppressWarnings("deprecation")
	public PasswordInvalidValidationError() {
		super(InvalidPasswordException.ERROR_LOGIN_INVALID_PASSWORD, LoginParams.FN_PASSWORD);
	}
}
