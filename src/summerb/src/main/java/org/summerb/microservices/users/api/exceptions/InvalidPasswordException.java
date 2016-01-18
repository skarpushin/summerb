package org.summerb.microservices.users.api.exceptions;

import org.summerb.approaches.i18n.HasMessageCode;

public class InvalidPasswordException extends UsersServiceException implements HasMessageCode {
	private static final long serialVersionUID = 2767210717179685812L;

	public static final String ERROR_LOGIN_INVALID_PASSWORD = "error.login.invalidPassword";

	@Override
	public String getMessageCode() {
		return ERROR_LOGIN_INVALID_PASSWORD;
	}

}
