package org.summerb.approaches.springmvc.security.dto;

import org.summerb.approaches.jdbccrud.common.DtoBase;

public class LoginParams implements DtoBase {
	private static final long serialVersionUID = 5895187972995172560L;
	public static final String FN_EMAIL = "email";
	public static final String FN_PASSWORD = "password";
	public static final String HEADER_REMEMBER_ME = "rememberMe";

	private String email;
	private String password;

	public LoginParams() {
	}

	public LoginParams(String login, String password) {
		this.email = login;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String login) {
		this.email = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
