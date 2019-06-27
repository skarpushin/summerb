package org.summerb.microservices.users.api;

import org.summerb.approaches.security.api.dto.NotAuthorizedResult;

public abstract class UsersMessageCodes {

	@Deprecated
	public static final String SECURITY_AUTHORIZATION_MISSING = NotAuthorizedResult.SECURITY_AUTHORIZATION_MISSING;
	@Deprecated
	public static final String SECURITY_AUTHORIZATION_MISSING_ON_SUBJECT = NotAuthorizedResult.SECURITY_AUTHORIZATION_MISSING_ON_SUBJECT;

}
