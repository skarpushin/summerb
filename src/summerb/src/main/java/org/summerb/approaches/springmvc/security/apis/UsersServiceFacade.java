package org.summerb.approaches.springmvc.security.apis;

import org.summerb.approaches.springmvc.security.dto.PasswordChange;
import org.summerb.approaches.springmvc.security.dto.PasswordReset;
import org.summerb.approaches.springmvc.security.dto.Registration;
import org.summerb.approaches.springmvc.security.dto.UserStatus;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.microservices.users.api.dto.User;
import org.summerb.microservices.users.api.exceptions.UserNotFoundException;
import org.summerb.utils.exceptions.GenericException;

/***
 * Typical set of operations related to user accounts in a web application
 * 
 * @author sergeyk
 *
 */
public interface UsersServiceFacade {
	User registerUser(Registration registration) throws FieldValidationException;

	void activateRegistration(String userUuid) throws GenericException;

	UserStatus getUserStatusByEmail(String email) throws FieldValidationException;

	String getNewPasswordResetToken(String email) throws FieldValidationException;

	User getUserByEmail(String email) throws UserNotFoundException, FieldValidationException;

	boolean isPasswordResetTokenValid(String userEmail, String passwordResetToken)
			throws UserNotFoundException, FieldValidationException;

	void resetPassword(String email, String passwordResetToken, PasswordReset resetPasswordRequest)
			throws UserNotFoundException, FieldValidationException;

	void changePassword(String email, PasswordChange passwordChange)
			throws UserNotFoundException, FieldValidationException;
}
