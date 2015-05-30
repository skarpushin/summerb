package org.summerb.security.api.exceptions;

import org.summerb.i18n.HasMessageCode;

public class CurrentUserNotFoundException extends RuntimeException implements HasMessageCode {
	private static final long serialVersionUID = 1899087866041906798L;

	public static final String ERROR_LOGIN_USER_NOT_FOUND = "sec.userNotLoggedIn";

	public CurrentUserNotFoundException() {
		super("Current user not found");
	}

	@Override
	public String getMessageCode() {
		return ERROR_LOGIN_USER_NOT_FOUND;
	}
}
