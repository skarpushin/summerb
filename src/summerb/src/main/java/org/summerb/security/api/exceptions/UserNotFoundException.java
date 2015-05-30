package org.summerb.security.api.exceptions;

import org.summerb.i18n.HasMessageCode;

public class UserNotFoundException extends Exception implements HasMessageCode {
	private static final long serialVersionUID = 1899087866041906798L;

	public static final String ERROR_LOGIN_USER_NOT_FOUND = "sec.login.userNotFound";

	private String userIdentifier;

	/**
	 * @deprecated constructor exists only for IO purposes
	 */
	@Deprecated
	public UserNotFoundException() {
	}

	public UserNotFoundException(String userIdentifier) {
		super("User not found: " + userIdentifier);
		this.userIdentifier = userIdentifier;
	}

	public String getUserIdentifier() {
		return userIdentifier;
	}

	@Override
	public String getMessageCode() {
		return ERROR_LOGIN_USER_NOT_FOUND;
	}
}
