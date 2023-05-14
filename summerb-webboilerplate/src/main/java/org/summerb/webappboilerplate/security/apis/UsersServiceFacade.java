/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.utils.exceptions.GenericException;
import org.summerb.validation.ValidationException;
import org.summerb.webappboilerplate.security.dto.PasswordChange;
import org.summerb.webappboilerplate.security.dto.PasswordReset;
import org.summerb.webappboilerplate.security.dto.Registration;
import org.summerb.webappboilerplate.security.dto.UserStatus;

/***
 * Typical set of operations related to user accounts in a web application
 * 
 * @author sergeyk
 *
 */
public interface UsersServiceFacade {
	User registerUser(Registration registration) throws ValidationException;

	void activateRegistration(String userUuid) throws GenericException;

	UserStatus getUserStatusByEmail(String email) throws ValidationException;

	String getNewPasswordResetToken(String email) throws ValidationException;

	User getUserByEmail(String email) throws UserNotFoundException, ValidationException;

	boolean isPasswordResetTokenValid(String userEmail, String passwordResetToken)
			throws UserNotFoundException, ValidationException;

	void resetPassword(String email, String passwordResetToken, PasswordReset resetPasswordRequest)
			throws UserNotFoundException, ValidationException;

	void changePassword(String email, PasswordChange passwordChange)
			throws UserNotFoundException, ValidationException;
}
