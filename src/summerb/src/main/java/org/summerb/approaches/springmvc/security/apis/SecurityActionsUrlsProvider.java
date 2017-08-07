package org.summerb.approaches.springmvc.security.apis;

/**
 * This interface provides url paths (excluding base path) for certain
 * controller actions.
 * 
 * @author sergeyk
 *
 */
public interface SecurityActionsUrlsProvider {
	String getLoginFormPath();

	String getLoginFailedPath();

	String getDefaultPath();

	String buildRegistrationActivationPath(String activationKey);

	String buildPasswordResetPath(String username, String passwordResetToken);
}
