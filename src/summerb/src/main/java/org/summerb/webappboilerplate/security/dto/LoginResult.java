package org.summerb.webappboilerplate.security.dto;

import java.util.Map;

import org.summerb.microservices.users.api.dto.User;
import org.summerb.utils.DtoBase;

public class LoginResult implements DtoBase {
	private static final long serialVersionUID = 252775263464590406L;

	public static final String ATTR_REDIRECT_TO = "redirectTo";

	private User user;
	private String rememberMeToken;

	/**
	 * Customer attributes
	 */
	private Map<String, Object> attributes;

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

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> tags) {
		this.attributes = tags;
	}

}
