package org.summerb.approaches.springmvc.security;

/**
 * List of message codes used on server side
 * 
 * @author skarpushin
 * 
 */
public class SecurityMessageCodes {
	public static final String INCORRECT_USER_NAME = "security.incorrectUserName";
	public static final String FAILED_TO_ACTIVATE_REGISTRATION = "security.failedToActivateRegistration";
	public static final String ALREADY_ACTIVATED = "security.alreadyActivated";
	public static final String NEED_ACTIVATION_TOKEN = "security.needActivationToken";
	public static final String REGISTRATION_ACTIVATED_OK = "security.registrationActivatedOk";
	public static final String REGISTRATION_ALREADY_STARTED = "security.registrationAlreadyStarted";
	public static final String AUTH_FATAL = "security.authFatal";
	public static final String CANT_LOGIN_UNTIL_REGISTRATION_ACTIVATED = "security.activationRequired";
	public static final String ACCOUNT_IS_NOT_CREATED_PLEASE_REGISTER = "security.registrationRequired";
	public static final String FAILED_TO_SEND_REGISTRATION_EMAIL = "security.failedToSendRegistrationEmail";
	public static final String FAILED_TO_SEND_PASSWORD_REST_EMAIL = "security.failedToSendPasswordResetEmail";
	public static final String INVALID_PASSWORD_RESET_TOKEN = "security.resetPasswordTokenInvalid";
	public static final String ACCOUNT_PASSWORD_RESET_OK = "security.passwordResetOk";
	public static final String VALIDATION_PASSWORDS_DO_NOT_MATCH = "security.secondPasswordDoesNotMatchFirst";
	public static final String BAD_CREDENTIALS = "security.badCredentials";

	public static final String LOGIN_REQUIRED = "security.authorizationRequired";
	public static final String ACCESS_DENIED = "security.accessDenied";
	public static final String ANONYMOUS = "security.anonymousUser";
	public static final String INVALID_SESSION = "security.invalidSession";

	public static final String NEW_PASSWORD = "term.newPassword";
	public static final String NEW_PASSWORD_AGAIN = "term.newPasswordAgain";
}
