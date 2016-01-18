package org.summerb.microservices.users.impl;

import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.mockito.Mockito;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.microservices.users.api.dto.AuthToken;
import org.summerb.microservices.users.api.dto.AuthTokenFactory;
import org.summerb.microservices.users.impl.dao.AuthTokenDao;

public class AuthTokenServiceDbImplFactory {

	public static final PagerParams pagerParamsUnexpectedException = new PagerParams(7, 9);

	public static AuthTokenServiceImpl createAuthTokenServiceDbImpl() {
		AuthTokenServiceImpl ret = new AuthTokenServiceImpl();

		ret.setPasswordService(PasswordServiceDbImplFactory.createPasswordServiceDbImpl());
		ret.setUserService(UserServiceImplFactory.createUsersServiceImpl());

		AuthTokenDao authTokenDao = Mockito.mock(AuthTokenDao.class);
		ret.setAuthTokenDao(authTokenDao);

		when(authTokenDao.findAuthTokenByUuid(AuthTokenFactory.AUTH_TOKEN_EXCEPTION))
				.thenThrow(new IllegalStateException("test simulate exception"));
		when(authTokenDao.findAuthTokenByUuid(AuthTokenFactory.AUTH_TOKEN_EXISTENT))
				.thenReturn(AuthTokenFactory.createAuthTokenForExistentUser());
		when(authTokenDao.findAuthTokenByUuid(AuthTokenFactory.AUTH_TOKEN_NOT_EXISTENT)).thenReturn(null);

		when(authTokenDao.findAuthTokenByUuid(AuthTokenFactory.AUTH_TOKEN_EXPIRED))
				.thenReturn(AuthTokenFactory.createExpiredAuthToken());

		List<AuthToken> expiredTokens = new LinkedList<AuthToken>();
		expiredTokens.add(AuthTokenFactory.createExpiredAuthToken());
		PaginatedList<AuthToken> expiredAuthTokens = new PaginatedList<AuthToken>(new PagerParams(), expiredTokens, 1);

		return ret;
	}
}