package org.summerb.security.api.exceptions;

import org.summerb.i18n.HasMessageCode;

public class AuthTokenNotFoundException extends Exception implements HasMessageCode {
	private static final long serialVersionUID = 6837239999953242494L;

	@Override
	public String getMessageCode() {
		return "sec.authToken.notFound";
	}

}
