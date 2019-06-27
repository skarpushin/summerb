package org.summerb.webappboilerplate.security.dto;

public class PasswordReset {
	public static final String FN_NEW_PASSWORD_AGAIN = "newPasswordAgain";

	private String password;
	private String newPasswordAgain;

	public String getPassword() {
		return password;
	}

	public void setPassword(String newPassword) {
		this.password = newPassword;
	}

	public String getNewPasswordAgain() {
		return newPasswordAgain;
	}

	public void setNewPasswordAgain(String newPasswordAgain) {
		this.newPasswordAgain = newPasswordAgain;
	}
}
