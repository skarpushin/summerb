package org.summerb.microservices.users.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.microservices.users.api.dto.User;
import org.summerb.microservices.users.api.dto.UserFactory;
import org.summerb.microservices.users.impl.dao.UserDao;

import com.google.common.eventbus.EventBus;

public class UserServiceImplFactory {

	public static UserServiceImpl createUsersServiceImpl() {
		UserServiceImpl ret = new UserServiceImpl();

		UserDao userDao = Mockito.mock(UserDao.class);
		ret.setUserDao(userDao);
		ret.setEventBus(Mockito.mock(EventBus.class));

		User existingUser = UserFactory.createExistingUser();

		when(userDao.findUserByUuid(UserFactory.EXISTENT_USER)).thenReturn(existingUser);
		when(userDao.findUserByUuid(UserFactory.EXISTENT_USER_2_PROBLEM_WITH_PASSWORD))
				.thenReturn(UserFactory.createExistingUser2());
		when(userDao.findUserByUuid(UserFactory.EXISTENT_USER_WITH_MISSING_PASSWORD))
				.thenReturn(UserFactory.createUserWithMissingPassword());
		when(userDao.findUserByUuid(UserFactory.NON_EXISTENT_USER)).thenReturn(null);
		when(userDao.findUserByUuid(UserFactory.USER_RESULT_IN_EXCEPTION))
				.thenThrow(new IllegalStateException("Simulate unexpected excception"));

		when(userDao.findUserByEmail(UserFactory.EXISTENT_USER_EMAIL)).thenReturn(existingUser);
		when(userDao.findUserByEmail(UserFactory.NON_EXISTENT_USER_EMAIL)).thenReturn(null);
		when(userDao.findUserByEmail(UserFactory.USER_EMAIL_RESULT_IN_EXCEPTION))
				.thenThrow(new IllegalStateException("Simulate unexpected excception"));

		when(userDao.findUserByDisplayNamePartial(eq(UserFactory.EXISTENT_USER), any(PagerParams.class)))
				.thenThrow(new IllegalStateException("Simulate unexpected excception"));

		when(userDao.findUserByUuid(UserFactory.EXISTENT_USER_WITH_EXPIRED_TOKEN))
				.thenReturn(UserFactory.createUserWithExpiredToken());

		when(userDao.updateUser(UserFactory.createDuplicateUser()))
				.thenThrow(new DuplicateKeyException("Simulate unexpected excception"));

		return ret;
	}
}