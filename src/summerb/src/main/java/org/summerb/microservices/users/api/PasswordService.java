package org.summerb.microservices.users.api;

import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.microservices.users.api.exceptions.UserNotFoundException;

/**
 * This service manages user passwords and restoration tokens.
 * 
 * Passwords functionality defined separately from @see {@link UserService}
 * because some implementation can create and update passwords, some could be
 * able to only verify user passwords. FOr example if will try to use ldap as
 * auth provider we'll be able to validate, but unable to modify user passwords
 * 
 * @author skarpushin
 * 
 */
public interface PasswordService {
	boolean isUserPasswordValid(String userUuid, String passwordPlain) throws UserNotFoundException;

	void setUserPassword(String userUuid, String newPasswordPlain)
			throws UserNotFoundException, FieldValidationException;

	String getNewRestorationTokenForUser(String userUuid) throws UserNotFoundException;

	boolean isRestorationTokenValid(String userUuid, String restorationTokenUuid) throws UserNotFoundException;

	void deleteRestorationToken(String userUuid) throws UserNotFoundException;
}
