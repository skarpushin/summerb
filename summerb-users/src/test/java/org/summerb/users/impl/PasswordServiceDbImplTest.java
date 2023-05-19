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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.summerb.users.api.PasswordService;
import org.summerb.users.api.dto.PasswordFactory;
import org.summerb.users.api.dto.UserFactory;
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.users.api.exceptions.UserServiceUnexpectedException;

public class PasswordServiceDbImplTest {

  @Test
  public void testIsUserPasswordValid_defensive_expectIllegalArgumentExceptionForUserUuid()
      throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.isUserPasswordValid(null, ""));
  }

  @Test
  public void testIsUserPasswordValid_defensive_expectIllegalArgumentExceptionForPassword()
      throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.isUserPasswordValid("", null));
  }

  @Test
  public void testIsUserPasswordValid_blackbox_expectUserUserNotFoundException() throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(
        UserNotFoundException.class,
        () -> fixture.isUserPasswordValid(UserFactory.NON_EXISTENT_USER, "validPassword"));
  }

  @Test
  public void testIsUserPasswordValid_blackbox_expectTrue() throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    boolean result =
        fixture.isUserPasswordValid(
            UserFactory.EXISTENT_USER, PasswordFactory.RIGHT_PASSWORD_FOR_EXISTENT_USER);
    assertTrue(result);
  }

  @Test
  public void testIsUserPasswordValid_blackbox_expectFalseOnNonWrongPassword() throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    boolean result = fixture.isUserPasswordValid(UserFactory.EXISTENT_USER, "some pwd");
    assertFalse(result);
  }

  @Test
  public void testIsUserPasswordValid_whitebox_expectFalseOnMissingPassword() throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    boolean result =
        fixture.isUserPasswordValid(UserFactory.EXISTENT_USER_WITH_MISSING_PASSWORD, "some pwd");
    assertFalse(result);
  }

  @Test
  public void testIsUserPasswordValid_whitebox_expectUsersServiceUnexpectedException()
      throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(
        UserServiceUnexpectedException.class,
        () ->
            fixture.isUserPasswordValid(
                UserFactory.EXISTENT_USER_2_PROBLEM_WITH_PASSWORD, "validPassword"));
  }

  @Test
  public void testSetUserPassword_defensive_nullUser() throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.setUserPassword(null, ""));
  }

  @Test
  public void testSetUserPassword_defensive_nullPassword() throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.setUserPassword("", null));
  }

  @Test
  public void testSetUserPassword_blackbox_expectUserNotFoundException() throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(
        UserNotFoundException.class,
        () -> fixture.setUserPassword(UserFactory.NON_EXISTENT_USER, "aaa"));
  }

  @Test
  public void testSetUserPassword_blackbox_expectUsersServiceUnexpectedException()
      throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(
        UserServiceUnexpectedException.class,
        () -> fixture.setUserPassword(UserFactory.USER_RESULT_IN_EXCEPTION, "aaa"));
  }

  @Test
  public void testSetUserPassword_blackbox_expectOk() throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    fixture.setUserPassword(UserFactory.EXISTENT_USER, "new password");
  }

  @Test
  public void testSetUserPassword_whitebox_expectUsersServiceUnexpectedExceptionOnDbProblem()
      throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(
        UserServiceUnexpectedException.class,
        () -> fixture.setUserPassword(UserFactory.EXISTENT_USER_WITH_MISSING_PASSWORD, "aaa"));
  }

  @Test
  public void testGetNewRestorationTokenForUser_defensive_nullUser() throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.getNewRestorationTokenForUser(null));
  }

  @Test
  public void testGetNewRestorationTokenForUser_blackbox_expectUserNotFoundException()
      throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(
        UserNotFoundException.class,
        () -> fixture.getNewRestorationTokenForUser(UserFactory.NON_EXISTENT_USER));
  }

  @Test
  public void testGetNewRestorationTokenForUser_blackbox_expectTokenWillBeReturned()
      throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    String result = fixture.getNewRestorationTokenForUser(UserFactory.EXISTENT_USER);
    assertNotNull(result);
  }

  @Test
  public void testGetNewRestorationTokenForUser_whitebox_expectUsersServiceUnexpectedException()
      throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(
        UserServiceUnexpectedException.class,
        () -> fixture.getNewRestorationTokenForUser(UserFactory.USER_RESULT_IN_EXCEPTION));
  }

  @Test
  public void
      testGetNewRestorationTokenForUser_whitebox_expectUsersServiceUnexpectedExceptionOnUnexpectedDbError()
          throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(
        UserServiceUnexpectedException.class,
        () ->
            fixture.getNewRestorationTokenForUser(UserFactory.EXISTENT_USER_WITH_MISSING_PASSWORD));
  }

  @Test
  public void testDeleteRestorationToken_defensive_userNull() throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.deleteRestorationToken(null));
  }

  @Test
  public void testDeleteRestorationToken_blackbox_expectUserNotFoundException() throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(
        UserNotFoundException.class,
        () -> fixture.deleteRestorationToken(UserFactory.NON_EXISTENT_USER));
  }

  @Test
  public void testDeleteRestorationToken_blackbox_expectOk() throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    fixture.deleteRestorationToken(UserFactory.EXISTENT_USER);
  }

  @Test
  public void testDeleteRestorationToken_whitebox_expectUsersServiceUnexpectedException()
      throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(
        UserServiceUnexpectedException.class,
        () -> fixture.deleteRestorationToken(UserFactory.USER_RESULT_IN_EXCEPTION));
  }

  @Test
  public void
      testDeleteRestorationToken_whitebox_expectUsersServiceUnexpectedExceptionOnUnexpectedDbProblem()
          throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(
        UserServiceUnexpectedException.class,
        () -> fixture.deleteRestorationToken(UserFactory.EXISTENT_USER_WITH_MISSING_PASSWORD));
  }

  @Test
  public void testIsRestorationTokenValid_defensive_userNull() throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.isRestorationTokenValid(null, ""));
  }

  @Test
  public void testIsRestorationTokenValid_defensive_tokenNull() throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.isRestorationTokenValid("", null));
  }

  @Test
  public void testIsRestorationTokenValid_blackbox_expectUserNotFoundException() throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(
        UserNotFoundException.class,
        () -> fixture.isRestorationTokenValid(UserFactory.NON_EXISTENT_USER, ""));
  }

  @Test
  public void testIsRestorationTokenValid_blackbox_expectFalseOnInvalidToken() throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    boolean result =
        fixture.isRestorationTokenValid(
            UserFactory.EXISTENT_USER, PasswordFactory.NOT_EXISTENT_RESTORATION_TOKEN);
    assertFalse(result);
  }

  @Test
  public void testIsRestorationTokenValid_blackbox_expectTrueOnValidToken() throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    boolean result =
        fixture.isRestorationTokenValid(
            UserFactory.EXISTENT_USER, PasswordFactory.TOKEN_FOR_EXISTENT_USER);
    assertTrue(result);
  }

  @Test
  public void testIsRestorationTokenValid_whitebox_expectFalseOnMissingToken() throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    boolean result =
        fixture.isRestorationTokenValid(
            UserFactory.EXISTENT_USER_WITH_MISSING_PASSWORD,
            PasswordFactory.TOKEN_FOR_EXISTENT_USER);
    assertFalse(result);
  }

  @Test
  public void testIsRestorationTokenValid_whitebox_expectUsersServiceUnexpectedException()
      throws Exception {
    PasswordService fixture = PasswordServiceDbImplFactory.createPasswordServiceDbImpl();
    assertThrows(
        UserServiceUnexpectedException.class,
        () ->
            fixture.isRestorationTokenValid(
                UserFactory.EXISTENT_USER_2_PROBLEM_WITH_PASSWORD, "asd"));
  }

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}
}
