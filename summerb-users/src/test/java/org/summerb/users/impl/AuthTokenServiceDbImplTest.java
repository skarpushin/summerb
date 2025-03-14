/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
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
  public void testCreateAuthToken_defensive_userNull() {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.authenticate(null, "", ""));
  }

  @Test
  public void testCreateAuthToken_defensive_passwordNull() {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.authenticate("", null, ""));
  }

  @Test
  public void testCreateAuthToken_defensive_clientIpNull() {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.authenticate("", "", null));
  }

  @Test
  public void testCreateAuthToken_blackbox_expectUserNotFoundExpcetion() {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        UserNotFoundException.class,
        () -> fixture.authenticate(UserFactory.NON_EXISTENT_USER_EMAIL, "", ""));
  }

  @Test
  public void testCreateAuthToken_blackbox_expectFieldValidationException() {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(ValidationException.class, () -> fixture.authenticate("abra cadabra", "", ""));
  }

  @Test
  public void testCreateAuthToken_blackbox_expectInvalidPasswordException() {
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
  public void testCreateAuthToken_whitebox_expectUserServiceUnexpectedExceptionOnDaoFail() {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        UserServiceUnexpectedException.class,
        () -> fixture.authenticate(UserFactory.USER_EMAIL_RESULT_IN_EXCEPTION, "", "0.0.0.0"));
  }

  @Test
  public void testGetAuthTokenByUuid_defensive_nullAuthToken() {
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
  public void testGetAuthTokenByUuid_blackbox_expectAuthTokenNotFoundException() {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        AuthTokenNotFoundException.class,
        () -> fixture.getAuthTokenByUuid(AuthTokenFactory.AUTH_TOKEN_NOT_EXISTENT));
  }

  @Test
  public void testGetAuthTokenByUuid_whitebox_expectUserServiceUnexpectedException() {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        UserServiceUnexpectedException.class,
        () -> fixture.getAuthTokenByUuid(AuthTokenFactory.AUTH_TOKEN_EXCEPTION));
  }

  @Test
  public void testIsAuthTokenValid_defensive_nullUserId() {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        IllegalArgumentException.class, () -> fixture.isAuthTokenValid(null, "...", "..."));
  }

  @Test
  public void testIsAuthTokenValid_defensive_nullAuthToken() {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.isAuthTokenValid("...", null, null));
  }

  @Test
  public void testIsAuthTokenValid_defensive_nullAuthTokenValue() {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        IllegalArgumentException.class, () -> fixture.isAuthTokenValid("...", "...", null));
  }

  @Test
  public void testIsAuthTokenValid_blackbox_expectUserNotFoundException() {
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
  public void testIsAuthTokenValid_whitebox_expectUserServiceUnexpectedException() {
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
  public void testDeleteAuthToken_defensive_nullAuthToken() {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.deleteAuthToken(null));
  }

  @Test
  public void testDeleteAuthToken_whitebox_expectOkForExistentToken() {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    fixture.deleteAuthToken(AuthTokenFactory.AUTH_TOKEN_EXISTENT);
  }

  @Test
  public void testDeleteAuthToken_whitebox_expectOkEvenForNonExistentToken() {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    fixture.deleteAuthToken(AuthTokenFactory.AUTH_TOKEN_NOT_EXISTENT);
  }

  @Test
  public void testDeleteAuthToken_whitebox_expectUserServiceUnexpectedException() {
    AuthTokenServiceImpl fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        UserServiceUnexpectedException.class,
        () -> fixture.deleteAuthToken(AuthTokenFactory.AUTH_TOKEN_EXCEPTION));
  }

  @Test
  public void testFindUserAuthTokens_defensive_nullUser() {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.findUserAuthTokens(null));
  }

  @Test
  public void testFindUserAuthTokens_whitebox_expectUserNotFoundExceptionForNonExistentUser() {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        UserNotFoundException.class,
        () -> fixture.findUserAuthTokens(UserFactory.NON_EXISTENT_USER));
  }

  @Test
  public void testFindUserAuthTokens_whitebox_expectUnexpectedException() {
    AuthTokenService fixture = AuthTokenServiceDbImplFactory.createAuthTokenServiceDbImpl();
    assertThrows(
        UserServiceUnexpectedException.class,
        () -> fixture.findUserAuthTokens(UserFactory.USER_RESULT_IN_EXCEPTION));
  }
}
