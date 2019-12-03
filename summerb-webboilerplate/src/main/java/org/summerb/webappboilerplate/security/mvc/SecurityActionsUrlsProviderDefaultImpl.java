/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.webappboilerplate.security.mvc;

import org.summerb.users.api.dto.User;
import org.summerb.webappboilerplate.security.apis.SecurityActionsUrlsProvider;

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
	public String buildRegistrationActivationPath(User user, String activationToken) {
		return ACTIVATE + "?" + PARAM_ACTIVATION_UUID + "=" + user.getUuid();
	}

	@Override
	public String buildPasswordResetPath(String username, String passwordResetToken) {
		return RESET_PASSWORD.replace("{passwordResetToken}", passwordResetToken) + "?" + User.FN_EMAIL + "="
				+ username;
	}
}
