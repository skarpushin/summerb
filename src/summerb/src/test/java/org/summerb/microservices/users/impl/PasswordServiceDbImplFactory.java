package org.summerb.microservices.users.impl;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.summerb.microservices.users.api.UserService;
import org.summerb.microservices.users.api.dto.PasswordFactory;
import org.summerb.microservices.users.api.dto.UserFactory;
import org.summerb.microservices.users.impl.dao.PasswordDao;

public class PasswordServiceDbImplFactory {
	private PasswordServiceDbImplFactory() {
	}

	public static PasswordServiceImpl createPasswordServiceDbImpl() {
		PasswordServiceImpl ret = new PasswordServiceImpl();

		ret.setPasswordEncoder(new StandardPasswordEncoder("test"));

		UserService userService = UserServiceImplFactory.createUsersServiceImpl();
		ret.setUserService(userService);

		PasswordDao passwordDao = Mockito.mock(PasswordDao.class);
		ret.setPasswordDao(passwordDao);

		when(passwordDao.findPasswordByUserUuid(UserFactory.EXISTENT_USER))
				.thenReturn(PasswordFactory.createExistentUserPassword());
		when(passwordDao.findPasswordByUserUuid(UserFactory.NON_EXISTENT_USER)).thenReturn(null);
		when(passwordDao.findPasswordByUserUuid(UserFactory.EXISTENT_USER_WITH_MISSING_PASSWORD)).thenReturn(null);
		when(passwordDao.findPasswordByUserUuid(UserFactory.USER_RESULT_IN_EXCEPTION))
				.thenThrow(new IllegalStateException("Simulate unexpected excception"));
		when(passwordDao.findPasswordByUserUuid(UserFactory.EXISTENT_USER_2_PROBLEM_WITH_PASSWORD))
				.thenThrow(new IllegalStateException("Simulate unexpected excception"));

		when(passwordDao.updateUserPassword(eq(UserFactory.EXISTENT_USER), anyString())).thenReturn(1);
		when(passwordDao.updateUserPassword(eq(UserFactory.EXISTENT_USER_WITH_MISSING_PASSWORD), anyString()))
				.thenReturn(0);

		when(passwordDao.setRestorationToken(eq(UserFactory.EXISTENT_USER), anyString())).thenReturn(1);
		when(passwordDao.setRestorationToken(eq(UserFactory.EXISTENT_USER_WITH_MISSING_PASSWORD), anyString()))
				.thenReturn(0);

		return ret;
	}
}