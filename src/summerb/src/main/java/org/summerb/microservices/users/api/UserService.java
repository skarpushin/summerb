package org.summerb.microservices.users.api;

import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.microservices.users.api.dto.User;
import org.summerb.microservices.users.api.exceptions.UserNotFoundException;

/**
 * Service for managing user accounts. That is. This is just a user registry.
 * 
 * Authorization is out of the scope bacause it is very domain specific and is
 * to be implemented by final application
 * 
 * @author skarpushin
 * 
 *         TODO: Unify tables naming to have common prefix, like "users_"
 */
public interface UserService {
	User createUser(User user) throws FieldValidationException;

	User getUserByUuid(String userUuid) throws UserNotFoundException;

	User getUserByEmail(String userEmail) throws FieldValidationException, UserNotFoundException;

	PaginatedList<User> findUsersByDisplayNamePartial(String displayNamePartial, PagerParams pagerParams)
			throws FieldValidationException;

	void updateUser(User user) throws FieldValidationException, UserNotFoundException;

	void deleteUserByUuid(String userUuid) throws UserNotFoundException;
}
