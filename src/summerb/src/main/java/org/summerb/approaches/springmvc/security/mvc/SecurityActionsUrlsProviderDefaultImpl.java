package org.summerb.approaches.springmvc.security.mvc;

import org.summerb.approaches.springmvc.security.apis.SecurityActionsUrlsProvider;
import org.summerb.microservices.users.api.dto.User;

/**
 * Default impl.
 * 
 * It's defined as a separate class (separate from {@link LoginController} to
 * have it in root context, rather in servlet context
 * 
 * See {@link SecurityActionsUrlsProvider}
 * 
 * @author sergeyk
 *
 */
public class SecurityActionsUrlsProviderDefaultImpl implements SecurityActionsUrlsProvider {
	public static final String PARAM_ACTIVATION_UUID = "activationUuid";

	public static final String LOGIN_FORM = "/login/form";
	public static final String LOGIN_FAILED = "/login/failed";
	public static final String CHANGE_PASSWORD = "/login/change";
	public static final String RESET_PASSWORD = "/login/reset/{passwordResetToken}";
	public static final String REQUEST_RESET = "/login/request-reset";
	public static final String ACTIVATE = "/login/activate";
	public static final String REGISTER = "/login/register";
	public static final String INVALID_SESSION = "/login/invalid-session";

	public SecurityActionsUrlsProviderDefaultImpl() {
	}

	@Override
	public String getLoginFormPath() {
		return LOGIN_FORM;
	}

	@Override
	public String getLoginFailedPath() {
		return LOGIN_FAILED;
	}

	@Override
	public String getChangePassword() {
		return CHANGE_PASSWORD;
	}

	@Override
	public String getRequestPasswordReset() {
		return REQUEST_RESET;
	}

	@Override
	public String getRegistration() {
		return REGISTER;
	}

	@Override
	public String getInvalidSession() {
		return INVALID_SESSION;
	}
	
	@Override
	public String getDefaultPath() {
		return "/";
	}

	@Override
	public String buildRegistrationActivationPath(String activationKey) {
		return ACTIVATE + "?" + PARAM_ACTIVATION_UUID + "=" + activationKey;
	}

	@Override
	public String buildPasswordResetPath(String username, String passwordResetToken) {
		return RESET_PASSWORD.replace("{passwordResetToken}", passwordResetToken) + "?" + User.FN_EMAIL + "="
				+ username;
	}
}
