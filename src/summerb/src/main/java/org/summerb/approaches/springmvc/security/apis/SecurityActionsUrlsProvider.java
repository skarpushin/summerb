package org.summerb.approaches.springmvc.security.apis;

import org.summerb.approaches.springmvc.security.mvc.LoginController;
import org.summerb.microservices.users.api.dto.User;

/**
 * This interface provides url paths (excluding base path) for certain
 * {@link LoginController} actions.
 * 
 * Impl supposed to be in sync with mappings specified in
 * {@link LoginController}. If mapping need to be changed, then you'll need to
 * create sub-class of {@link LoginController} and override RequestMapping where
 * needed
 * 
 * @author sergeyk
 *
 */
public interface SecurityActionsUrlsProvider {
	String getLoginFormPath();

	String getLoginFailedPath();

	String getDefaultPath();

	String buildRegistrationActivationPath(User user, String activationToken);

	String buildPasswordResetPath(String username, String passwordResetToken);

	String getChangePassword();

	String getRequestPasswordReset();

	String getRegistration();

	String getInvalidSession();
}
