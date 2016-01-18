package org.summerb.microservices.users.api.exceptions;

import org.summerb.approaches.i18n.HasMessageCode;

public class AuthTokenNotFoundException extends UsersServiceException implements HasMessageCode {
	private static final long serialVersionUID = 6837239999953242494L;

	@Override
	public String getMessageCode() {
		return "exception.authToken.notFound";
	}

}
