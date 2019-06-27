package org.summerb.webappboilerplate.security.apis;

/**
 * View names provider for the Security functions
 * 
 * @author sergeyk
 *
 */
public interface SecurityViewNamesProvider {
	String homePage();

	String loginForm();

	String registerForm();

	String activateRegistration();

	String resetPasswordRequest();

	/**
	 * View name that is used to actually reset password. Activated only using
	 * restPassword token (usually from email)
	 */
	String resetPassword();

	String changePassword();
}
