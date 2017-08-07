package org.summerb.approaches.springmvc.security.dto;

import org.summerb.approaches.jdbccrud.common.DtoBase;
import org.summerb.microservices.users.api.dto.User;

public class LoginResult implements DtoBase {
	private static final long serialVersionUID = 252775263464590406L;

	private User user;
	private String rememberMeToken;

	public LoginResult() {
	}

	public LoginResult(User user, String rememberMeToken) {
		this.rememberMeToken = rememberMeToken;
		this.user = user;
	}

	public String getRememberMeToken() {
		return rememberMeToken;
	}

	public void setRememberMeToken(String authToken) {
		this.rememberMeToken = authToken;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
