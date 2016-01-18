package org.summerb.microservices.users.api.exceptions;

public abstract class UsersServiceException extends Exception {
	private static final long serialVersionUID = -8926908954341351477L;

	public UsersServiceException() {
	}

	public UsersServiceException(String message) {
		super(message);
	}
}
