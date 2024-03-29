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
package org.summerb.users.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.summerb.users.api.AuthTokenService;
import org.summerb.users.api.dto.AuthToken;
import org.summerb.users.api.dto.AuthTokenFactory;
import org.summerb.users.api.dto.PasswordFactory;
import org.summerb.users.api.dto.UserFactory;
import org.summerb.users.api.exceptions.AuthTokenNotFoundException;
import org.summerb.users.api.exceptions.InvalidPasswordException;
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.users.api.exceptions.UserServiceUnexpectedException;
import org.summerb.validation.ValidationException;

public class AuthTokenServiceDbImplTest {

  @Test
  public void testCreateAuthToken_defensive_userNull() throws Exception {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.authenticate(null, "", ""));
  }

  @Test
  public void testCreateAuthToken_defensive_passwordNull() throws Exception {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.authenticate("", null, ""));
  }

  @Test
  public void testCreateAuthToken_defensive_clientIpNull() throws Exception {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.authenticate("", "", null));
  }

  @Test
  public void testCreateAuthToken_blackbox_expectUserNotFoundExpcetion() throws Exception {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        UserNotFoundException.class,
        () -> fixture.authenticate(UserFactory.NON_EXISTENT_USER_EMAIL, "", ""));
  }

  @Test
  public void testCreateAuthToken_blackbox_expectFieldValidationException() throws Exception {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(ValidationException.class, () -> fixture.authenticate("abra cadabra", "", ""));
  }

  @Test
  public void testCreateAuthToken_blackbox_expectInvalidPasswordException() throws Exception {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        InvalidPasswordException.class,
        () ->
            fixture.authenticate(
                UserFactory.EXISTENT_USER_EMAIL, UUID.randomUUID().toString(), ""));
  }

  @Test
  public void testCreateAuthToken_blackbox_expectNewAuthToken() throws Exception {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    AuthToken result =
        fixture.authenticate(
            UserFactory.EXISTENT_USER_EMAIL,
            PasswordFactory.RIGHT_PASSWORD_FOR_EXISTENT_USER,
            "0.0.0.0");
    assertNotNull(result);
  }

  @Test
  public void testCreateAuthToken_whitebox_expectUserServiceUnexpectedExceptionOnDaoFail()
      throws Exception {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        UserServiceUnexpectedException.class,
        () -> fixture.authenticate(UserFactory.USER_EMAIL_RESULT_IN_EXCEPTION, "", "0.0.0.0"));
  }

  @Test
  public void testGetAuthTokenByUuid_defensive_nullAuthToken() throws Exception {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.getAuthTokenByUuid(null));
  }

  @Test
  public void testGetAuthTokenByUuid_blackbox_expectOk() throws Exception {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    AuthToken result = fixture.getAuthTokenByUuid(AuthTokenFactory.AUTH_TOKEN_EXISTENT);
    assertNotNull(result);
  }

  @Test
  public void testGetAuthTokenByUuid_blackbox_expectAuthTokenNotFoundException() throws Exception {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        AuthTokenNotFoundException.class,
        () -> fixture.getAuthTokenByUuid(AuthTokenFactory.AUTH_TOKEN_NOT_EXISTENT));
  }

  @Test
  public void testGetAuthTokenByUuid_whitebox_expectUserServiceUnexpectedException()
      throws Exception {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        UserServiceUnexpectedException.class,
        () -> fixture.getAuthTokenByUuid(AuthTokenFactory.AUTH_TOKEN_EXCEPTION));
  }

  @Test
  public void testIsAuthTokenValid_defensive_nullUserId() throws Exception {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        IllegalArgumentException.class, () -> fixture.isAuthTokenValid(null, "...", "..."));
  }

  @Test
  public void testIsAuthTokenValid_defensive_nullAuthToken() throws Exception {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.isAuthTokenValid("...", null, null));
  }

  @Test
  public void testIsAuthTokenValid_defensive_nullAuthTokenValue() throws Exception {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        IllegalArgumentException.class, () -> fixture.isAuthTokenValid("...", "...", null));
  }

  @Test
  public void testIsAuthTokenValid_blackbox_expectUserNotFoundException() throws Exception {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        UserNotFoundException.class,
        () ->
            fixture.isAuthTokenValid(
                UserFactory.NON_EXISTENT_USER,
                AuthTokenFactory.AUTH_TOKEN_EXISTENT,
                AuthTokenFactory.AUTH_TOKEN_EXISTENT_VALUE));
  }

  @Test
  public void testIsAuthTokenValid_blackbox_expectFalseEvenForNonExistentToken() throws Exception {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    AuthToken result =
        fixture.isAuthTokenValid(
            UserFactory.EXISTENT_USER, AuthTokenFactory.AUTH_TOKEN_NOT_EXISTENT, "...");
    assertNull(result);
  }

  @Test
  public void testIsAuthTokenValid_blackbox_expectOkForExistentToken() throws Exception {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    AuthToken result =
        fixture.isAuthTokenValid(
            UserFactory.EXISTENT_USER,
            AuthTokenFactory.AUTH_TOKEN_EXISTENT,
            AuthTokenFactory.AUTH_TOKEN_EXISTENT_VALUE);
    assertNotNull(result);
  }

  @Test
  public void testIsAuthTokenValid_blackbox_expectFalseForExpiredToken() throws Exception {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    AuthToken result =
        fixture.isAuthTokenValid(
            UserFactory.EXISTENT_USER_WITH_EXPIRED_TOKEN,
            AuthTokenFactory.AUTH_TOKEN_EXPIRED,
            "...");
    assertNull(result);
  }

  @Test
  public void testIsAuthTokenValid_whitebox_expectUserServiceUnexpectedException()
      throws Exception {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        UserServiceUnexpectedException.class,
        () ->
            fixture.isAuthTokenValid(
                UserFactory.USER_RESULT_IN_EXCEPTION,
                AuthTokenFactory.AUTH_TOKEN_EXISTENT,
                AuthTokenFactory.AUTH_TOKEN_EXISTENT_VALUE));
  }

  @Test
  public void testIsAuthTokenValid_whitebox_expectFalseForUnmatchedAuthTOken() throws Exception {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    AuthToken result =
        fixture.isAuthTokenValid(
            UserFactory.EXISTENT_USER, AuthTokenFactory.AUTH_TOKEN_EXPIRED, "...");
    assertNull(result);
  }

  @Test
  public void testDeleteAuthToken_defensive_nullAuthToken() throws Exception {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.deleteAuthToken(null));
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

  @Test
  public void testDeleteAuthToken_whitebox_expectUserServiceUnexpectedException() throws Exception {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        UserServiceUnexpectedException.class,
        () -> fixture.deleteAuthToken(AuthTokenFactory.AUTH_TOKEN_EXCEPTION));
  }

  @Test
  public void testFindUserAuthTokens_defensive_nullUser() throws Exception {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.findUserAuthTokens(null));
  }

  @Test
  public void testFindUserAuthTokens_whitebox_expectUserNotFoundExceptionForNonExistentUser()
      throws Exception {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        UserNotFoundException.class,
        () -> fixture.findUserAuthTokens(UserFactory.NON_EXISTENT_USER));
  }

  @Test
  public void testFindUserAuthTokens_whitebox_expectUnexpectedException() throws Exception {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        UserServiceUnexpectedException.class,
        () -> fixture.findUserAuthTokens(UserFactory.USER_RESULT_IN_EXCEPTION));
  }

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}
}
