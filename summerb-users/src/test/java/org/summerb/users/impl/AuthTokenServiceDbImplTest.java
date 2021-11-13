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
package org.summerb.users.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.summerb.users.api.AuthTokenService;
import org.summerb.users.api.dto.AuthToken;
import org.summerb.users.api.dto.AuthTokenFactory;
import org.summerb.users.api.dto.PasswordFactory;
import org.summerb.users.api.dto.UserFactory;
import org.summerb.users.api.exceptions.AuthTokenNotFoundException;
import org.summerb.users.api.exceptions.InvalidPasswordException;
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.users.api.exceptions.UserServiceUnexpectedException;
import org.summerb.validation.FieldValidationException;

public class AuthTokenServiceDbImplTest {

	@Test(expected = IllegalArgumentException.class)
	public void testCreateAuthToken_defensive_userNull() throws Exception {
		AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.authenticate(null, "", "");
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateAuthToken_defensive_passwordNull() throws Exception {
		AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.authenticate("", null, "");
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateAuthToken_defensive_clientIpNull() throws Exception {
		AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.authenticate("", "", null);
		fail();
	}

	@Test(expected = UserNotFoundException.class)
	public void testCreateAuthToken_blackbox_expectUserNotFoundExpcetion() throws Exception {
		AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.authenticate(UserFactory.NON_EXISTENT_USER_EMAIL, "", "");
		fail();
	}

	@Test(expected = FieldValidationException.class)
	public void testCreateAuthToken_blackbox_expectFieldValidationException() throws Exception {
		AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.authenticate("abra cadabra", "", "");
		fail();
	}

	@Test(expected = InvalidPasswordException.class)
	public void testCreateAuthToken_blackbox_expectInvalidPasswordException() throws Exception {
		AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.authenticate(UserFactory.EXISTENT_USER_EMAIL, UUID.randomUUID().toString(), "");
		fail();
	}

	@Test
	public void testCreateAuthToken_blackbox_expectNewAuthToken() throws Exception {
		AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		AuthToken result = fixture.authenticate(UserFactory.EXISTENT_USER_EMAIL,
				PasswordFactory.RIGHT_PASSWORD_FOR_EXISTENT_USER, "0.0.0.0");
		assertNotNull(result);
	}

	@Test(expected = UserServiceUnexpectedException.class)
	public void testCreateAuthToken_whitebox_expectUserServiceUnexpectedExceptionOnDaoFail() throws Exception {
		AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.authenticate(UserFactory.USER_EMAIL_RESULT_IN_EXCEPTION, "", "0.0.0.0");
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetAuthTokenByUuid_defensive_nullAuthToken() throws Exception {
		AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.getAuthTokenByUuid(null);
		fail();
	}

	@Test
	public void testGetAuthTokenByUuid_blackbox_expectOk() throws Exception {
		AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		AuthToken result = fixture.getAuthTokenByUuid(AuthTokenFactory.AUTH_TOKEN_EXISTENT);
		assertNotNull(result);
	}

	@Test(expected = AuthTokenNotFoundException.class)
	public void testGetAuthTokenByUuid_blackbox_expectAuthTokenNotFoundException() throws Exception {
		AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.getAuthTokenByUuid(AuthTokenFactory.AUTH_TOKEN_NOT_EXISTENT);
		fail();
	}

	@Test(expected = UserServiceUnexpectedException.class)
	public void testGetAuthTokenByUuid_whitebox_expectUserServiceUnexpectedException() throws Exception {
		AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.getAuthTokenByUuid(AuthTokenFactory.AUTH_TOKEN_EXCEPTION);
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsAuthTokenValid_defensive_nullUserId() throws Exception {
		AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.isAuthTokenValid(null, "...", "...");
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsAuthTokenValid_defensive_nullAuthToken() throws Exception {
		AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.isAuthTokenValid("...", null, null);
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsAuthTokenValid_defensive_nullAuthTokenValue() throws Exception {
		AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.isAuthTokenValid("...", "...", null);
		fail();
	}

	@Test(expected = UserNotFoundException.class)
	public void testIsAuthTokenValid_blackbox_expectUserNotFoundException() throws Exception {
		AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.isAuthTokenValid(UserFactory.NON_EXISTENT_USER, AuthTokenFactory.AUTH_TOKEN_EXISTENT,
				AuthTokenFactory.AUTH_TOKEN_EXISTENT_VALUE);
		fail();
	}

	@Test
	public void testIsAuthTokenValid_blackbox_expectFalseEvenForNonExistentToken() throws Exception {
		AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		AuthToken result = fixture.isAuthTokenValid(UserFactory.EXISTENT_USER, AuthTokenFactory.AUTH_TOKEN_NOT_EXISTENT,
				"...");
		assertNull(result);
	}

	@Test
	public void testIsAuthTokenValid_blackbox_expectOkForExistentToken() throws Exception {
		AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		AuthToken result = fixture.isAuthTokenValid(UserFactory.EXISTENT_USER, AuthTokenFactory.AUTH_TOKEN_EXISTENT,
				AuthTokenFactory.AUTH_TOKEN_EXISTENT_VALUE);
		assertNotNull(result);
	}

	@Test
	public void testIsAuthTokenValid_blackbox_expectFalseForExpiredToken() throws Exception {
		AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		AuthToken result = fixture.isAuthTokenValid(UserFactory.EXISTENT_USER_WITH_EXPIRED_TOKEN,
				AuthTokenFactory.AUTH_TOKEN_EXPIRED, "...");
		assertNull(result);
	}

	@Test(expected = UserServiceUnexpectedException.class)
	public void testIsAuthTokenValid_whitebox_expectUserServiceUnexpectedException() throws Exception {
		AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.isAuthTokenValid(UserFactory.USER_RESULT_IN_EXCEPTION, AuthTokenFactory.AUTH_TOKEN_EXISTENT,
				AuthTokenFactory.AUTH_TOKEN_EXISTENT_VALUE);
		fail();
	}

	@Test
	public void testIsAuthTokenValid_whitebox_expectFalseForUnmatchedAuthTOken() throws Exception {
		AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		AuthToken result = fixture.isAuthTokenValid(UserFactory.EXISTENT_USER, AuthTokenFactory.AUTH_TOKEN_EXPIRED,
				"...");
		assertNull(result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteAuthToken_defensive_nullAuthToken() throws Exception {
		AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.deleteAuthToken(null);
		fail();
	}

	@Test
	public void testDeleteAuthToken_whitebox_expectOkForExistentToken() throws Exception {
		AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.deleteAuthToken(AuthTokenFactory.AUTH_TOKEN_EXISTENT);
	}

	@Test
	public void testDeleteAuthToken_whitebox_expectOkEvenForNonExistentToken() throws Exception {
		AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.deleteAuthToken(AuthTokenFactory.AUTH_TOKEN_NOT_EXISTENT);
	}

	@Test(expected = UserServiceUnexpectedException.class)
	public void testDeleteAuthToken_whitebox_expectUserServiceUnexpectedException() throws Exception {
		AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.deleteAuthToken(AuthTokenFactory.AUTH_TOKEN_EXCEPTION);
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindUserAuthTokens_defensive_nullUser() throws Exception {
		AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.findUserAuthTokens(null);
		fail();
	}

	@Test(expected = UserNotFoundException.class)
	public void testFindUserAuthTokens_whitebox_expectUserNotFoundExceptionForNonExistentUser() throws Exception {
		AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.findUserAuthTokens(UserFactory.NON_EXISTENT_USER);
		fail();
	}

	@Test(expected = UserServiceUnexpectedException.class)
	public void testFindUserAuthTokens_whitebox_expectUnexpectedException() throws Exception {
		AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
		fixture.findUserAuthTokens(UserFactory.USER_RESULT_IN_EXCEPTION);
		fail();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

}
