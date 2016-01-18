package org.summerb.microservices.users.impl.dao;

import org.springframework.dao.DuplicateKeyException;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.microservices.users.api.dto.User;

public interface UserDao {
	void createUser(User user) throws DuplicateKeyException;

	User findUserByUuid(String userUuid);

	User findUserByEmail(String userEmail);

	PaginatedList<User> findUserByDisplayNamePartial(String displayNamePartial, PagerParams pagerParams);

	boolean updateUser(User user) throws DuplicateKeyException;

	boolean deleteUser(String userUuid);
}
