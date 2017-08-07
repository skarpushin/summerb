package org.summerb.approaches.springmvc.security.ve;

import org.summerb.approaches.validation.ValidationError;
import org.summerb.microservices.users.api.dto.User;
import org.summerb.microservices.users.api.exceptions.UserNotFoundException;

public class UserNotFoundValidationError extends ValidationError {
	private static final long serialVersionUID = 5184851404690565907L;

	@SuppressWarnings("deprecation")
	public UserNotFoundValidationError() {
		super(UserNotFoundException.ERROR_LOGIN_USER_NOT_FOUND, User.FN_EMAIL);
	}
}
