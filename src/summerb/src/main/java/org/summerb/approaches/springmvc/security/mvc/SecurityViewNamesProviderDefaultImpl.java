package org.summerb.approaches.springmvc.security.mvc;

import org.summerb.approaches.springmvc.security.apis.SecurityViewNamesProvider;

public class SecurityViewNamesProviderDefaultImpl extends org.summerb.approaches.springmvc.Views
		implements SecurityViewNamesProvider {

	protected String viewsBasePath = "login";

	@Override
	public String homePage() {
		return "site/index";
	}

	@Override
	public String loginForm() {
		return viewsBasePath + "/login";
	}

	@Override
	public String registerForm() {
		return viewsBasePath + "/register";
	}

	@Override
	public String activateRegistration() {
		return viewsBasePath + "/activate";
	}

	@Override
	public String resetPasswordRequest() {
		return viewsBasePath + "/request-reset";
	}

	@Override
	public String resetPassword() {
		return viewsBasePath + "/reset";
	}

	@Override
	public String changePassword() {
		return viewsBasePath + "/change";
	}
}
