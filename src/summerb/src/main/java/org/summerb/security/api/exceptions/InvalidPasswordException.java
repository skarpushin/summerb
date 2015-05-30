package org.summerb.security.api.exceptions;

import org.summerb.i18n.HasMessageCode;

public class InvalidPasswordException extends Exception implements HasMessageCode {
	private static final long serialVersionUID = 2767210717179685812L;

	public static final String ERROR_LOGIN_INVALID_PASSWORD = "sec.login.invalidPassword";

	@Override
	public String getMessageCode() {
		return ERROR_LOGIN_INVALID_PASSWORD;
	}

}
