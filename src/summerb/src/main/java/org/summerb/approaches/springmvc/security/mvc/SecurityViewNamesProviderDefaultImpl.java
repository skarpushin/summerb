package org.summerb.approaches.springmvc.security.mvc;

import org.summerb.approaches.springmvc.security.apis.SecurityViewNamesProvider;

public class SecurityViewNamesProviderDefaultImpl extends org.summerb.approaches.springmvc.Views
		implements SecurityViewNamesProvider {
	@Override
	public String homePage() {
		return "site/index";
	}

	@Override
	public String loginForm() {
		return "login/login";
	}

	@Override
	public String registerForm() {
		return "login/register";
	}

	@Override
	public String activateRegistration() {
		return "login/activate";
	}

	@Override
	public String resetPasswordRequest() {
		return "login/request-reset";
	}

	@Override
	public String resetPassword() {
		return "login/reset";
	}

	@Override
	public String changePassword() {
		return "login/change";
	}
}
