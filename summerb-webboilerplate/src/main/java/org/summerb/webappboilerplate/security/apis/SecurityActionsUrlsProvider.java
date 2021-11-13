/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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
package org.summerb.webappboilerplate.security.apis;

import org.summerb.users.api.dto.User;
import org.summerb.webappboilerplate.security.mvc.LoginController;

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
