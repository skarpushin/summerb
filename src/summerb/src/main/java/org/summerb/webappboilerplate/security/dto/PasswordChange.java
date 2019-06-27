package org.summerb.webappboilerplate.security.dto;

public class PasswordChange {
	public static final String FN_NEW_PASSWORD_AGAIN = "newPasswordAgain";
	public static final String FN_CURRENT_PASSWORD = "currentPassword";

	private String currentPassword;
	/**
	 * IMPORTANT: This is a little hack, but thi field is intentionally called
	 * password, but not newPassword, in order to avoid the need of
	 * FieldValidationException mapping comed from User service
	 */
	private String password;
	private String newPasswordAgain;

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String newPassword) {
		this.currentPassword = newPassword;
	}

	public String getNewPasswordAgain() {
		return newPasswordAgain;
	}

	public void setNewPasswordAgain(String newPasswordAgain) {
		this.newPasswordAgain = newPasswordAgain;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String newPassword) {
		this.password = newPassword;
	}
}
