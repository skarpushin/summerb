package org.summerb.microservices.users.api.exceptions;

import org.summerb.approaches.i18n.HasMessageCode;

public class UserServiceUnexpectedException extends RuntimeException implements HasMessageCode {
	private static final long serialVersionUID = 663561647374456299L;

	public UserServiceUnexpectedException() {
	}

	public UserServiceUnexpectedException(String technicalMessage, Throwable cause) {
		super(technicalMessage, cause);
	}

	@Override
	public String getMessageCode() {
		return "userService.unexpectedException";
	}
}
