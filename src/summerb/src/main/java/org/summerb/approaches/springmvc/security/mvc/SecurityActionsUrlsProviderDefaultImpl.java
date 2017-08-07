package org.summerb.approaches.springmvc.security.mvc;

import org.summerb.approaches.springmvc.security.apis.SecurityActionsUrlsProvider;
import org.summerb.approaches.springmvc.security.apis.SecurityViewNamesProvider;
import org.summerb.microservices.users.api.dto.User;

import com.google.common.base.Preconditions;

/**
 * The only reason to have this impl separate it from loginController is to
 * decouple servlet and root context.
 * 
 * This impl is anticipated to be tightly related to LoginController thus have
 * to be in sync with latter RequestMappings.
 * 
 * @author sergeyk
 *
 */
public class SecurityActionsUrlsProviderDefaultImpl implements SecurityActionsUrlsProvider {
	public static final String PARAM_ACTIVATION_UUID = "activationUuid";
	public static final String URL_LOGIN_FAILED = "/login/failed";
	public static final String URL_LOGIN_FORM = "/login/form";

	private SecurityViewNamesProvider views;

	public SecurityActionsUrlsProviderDefaultImpl() {
		views = new SecurityViewNamesProviderDefaultImpl();
	}

	public SecurityActionsUrlsProviderDefaultImpl(SecurityViewNamesProvider views) {
		Preconditions.checkArgument(views != null, "Views required");
		this.views = views;
	}

	@Override
	public String getLoginFormPath() {
		return URL_LOGIN_FORM;
	}

	@Override
	public String getLoginFailedPath() {
		return URL_LOGIN_FAILED;
	}

	@Override
	public String getDefaultPath() {
		return "/";
	}

	@Override
	public String buildRegistrationActivationPath(String activationKey) {
		return "/" + views.activateRegistration() + "?" + PARAM_ACTIVATION_UUID + "=" + activationKey;
	}

	@Override
	public String buildPasswordResetPath(String username, String passwordResetToken) {
		return "/" + views.resetPassword() + "/" + passwordResetToken + "?" + User.FN_EMAIL + "=" + username;
	}

}
