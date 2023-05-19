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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.LinkedList;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.users.api.dto.User;
import org.summerb.users.api.dto.UserFactory;
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.users.api.exceptions.UserServiceUnexpectedException;
import org.summerb.validation.ValidationException;

/** NOTE: These tests does not follow test names convention */
public class UserServiceImplTest {

  @Test
  public void testCreateUser_blackbox_expectGuidWillBeCreatedForTheUser() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

    User result = fixture.createUser(UserFactory.createNewUserTemplate());

    // add additional test code here
    assertNotNull(result);
    assertNotNull(result.getUuid());
  }

  @Test
  public void testCreateUser_blackbox_expectAllFieldsEqualsForCreatedUser() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

    User user = UserFactory.createNewUserTemplate();
    User createdUser = fixture.createUser(user);

    // add additional test code here
    assertNotNull(createdUser);

    assertEquals(createdUser.getDisplayName(), user.getDisplayName());
    assertEquals(createdUser.getEmail(), user.getEmail());
    assertEquals(createdUser.getIntegrationData(), user.getIntegrationData());
    assertEquals(createdUser.getIsBlocked(), user.getIsBlocked());
    assertEquals(createdUser.getLocale(), user.getLocale());
    assertEquals(createdUser.getRegisteredAt(), user.getRegisteredAt());
    assertEquals(createdUser.getTimeZone(), user.getTimeZone());
  }

  @Test
  public void testCreateUser_blackbox_expectRegisteredAtWillBePopulated() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

    User user = UserFactory.createNewUserTemplate();
    user.setRegisteredAt(0);
    User createdUser = fixture.createUser(user);

    // add additional test code here
    assertNotNull(createdUser);
    assertTrue(createdUser.getRegisteredAt() != 0L);
  }

  @Test
  public void testCreateUser_whitebox_expectReturnedValueIsNotTheSameReferenceAsParameter()
      throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

    User template = UserFactory.createNewUserTemplate();
    User result = fixture.createUser(template);

    // add additional test code here
    assertNotNull(result);
    assertNotSame(template, result);
  }

  @Test
  public void testCreateUser_blackbox_expectValidationExceptionOnInvalidEmail() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

    User user = UserFactory.createNewUserTemplate();
    user.setEmail("abara-cadabara");

    assertThrows(ValidationException.class, () -> fixture.createUser(user));
  }

  @Test
  public void testCreateUser_blackbox_expectValidationExceptionOnInvalidEmail1() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

    User user = UserFactory.createNewUserTemplate();
    user.setEmail(null);

    assertThrows(ValidationException.class, () -> fixture.createUser(user));
  }

  @Test
  public void testCreateUser_whitebox_expectDuplicateUserException() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

    User user = UserFactory.createNewUserTemplate();
    doThrow(new DuplicateKeyException("duplicate user"))
        .when(fixture.getUserDao())
        .createUser(any(User.class));

    assertThrows(ValidationException.class, () -> fixture.createUser(user));
  }

  @Test
  public void testCreateUser_whitebox_expectOurExceptionInsteadOfUnexpectedOne() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

    User user = UserFactory.createNewUserTemplate();
    doThrow(new IllegalArgumentException("test exception"))
        .when(fixture.getUserDao())
        .createUser(any(User.class));

    assertThrows(UserServiceUnexpectedException.class, () -> fixture.createUser(user));
  }

  @Test
  public void testCreateUser_defensive_expectIllegalArgumentExceptionInsteadOfNpe()
      throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

    assertThrows(IllegalArgumentException.class, () -> fixture.createUser(null));
  }

  @Test
  public void testGetUserByUuid_defensive_expectIllegalArgumentExceptionInsteadOfNpe1()
      throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.getUserByUuid(null));
  }

  @Test
  public void testGetUserByUuid_defensive_expectIllegalArgumentExceptionInsteadOfNpe2()
      throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.getUserByUuid(""));
  }

  @Test
  public void testGetUserByUuid_whitebox_expectOurExceptionInsteadOfUnexpectedOne()
      throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

    doThrow(new IllegalArgumentException("test exception"))
        .when(fixture.getUserDao())
        .findUserByUuid(any(String.class));

    assertThrows(UserServiceUnexpectedException.class, () -> fixture.getUserByUuid("aaa"));
  }

  @Test
  public void testGetUserByUuid_blackbox_expectUserNotFoundException() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

    when(fixture.getUserDao().findUserByUuid(any(String.class))).thenReturn(null);

    assertThrows(UserNotFoundException.class, () -> fixture.getUserByUuid("aaa"));
  }

  @Test
  public void testGetUserByUuid_blackbox_expectUserWasFound() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

    when(fixture.getUserDao().findUserByUuid(any(String.class)))
        .thenReturn(UserFactory.createNewUserTemplate());

    assertNotNull(fixture.getUserByUuid("aaa"));
  }

  @Test
  public void testGetUserByEmail_defensive_expectIllegalArgumentExceptionOnNull() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.getUserByEmail(null));
  }

  @Test
  public void testGetUserByEmail_blackbox_expectFieldValidationExceptionOnEmptyString()
      throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    assertThrows(ValidationException.class, () -> fixture.getUserByEmail(""));
  }

  @Test
  public void testGetUserByEmail_blackbox_expectFieldValidationExceptionOnWrongFormat()
      throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    assertThrows(ValidationException.class, () -> fixture.getUserByEmail("abara-cadabara"));
  }

  @Test
  public void testGetUserByEmail_whitebox_expectOurExceptionOnUnexpectedOne() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    doThrow(new IllegalArgumentException("test exception"))
        .when(fixture.getUserDao())
        .findUserByEmail(any(String.class));

    assertThrows(
        UserServiceUnexpectedException.class,
        () -> fixture.getUserByEmail(UserFactory.createNewUserTemplate().getEmail()));
  }

  @Test
  public void testGetUserByEmail_blackbox_expectUserNotFoundException() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

    when(fixture.getUserDao().findUserByEmail(any(String.class))).thenReturn(null);

    assertThrows(
        UserNotFoundException.class,
        () -> fixture.getUserByEmail(UserFactory.createNewUserTemplate().getEmail()));
  }

  @Test
  public void testGetUserByEmail_blackbox_expectFoundUser() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

    when(fixture.getUserDao().findUserByEmail(any(String.class)))
        .thenReturn(UserFactory.createNewUserTemplate());
    User user = fixture.getUserByEmail(UserFactory.createNewUserTemplate().getEmail());

    assertNotNull(user);
  }

  @Test
  public void
      testFindUsersByDisplayNamePartial_defensive_shouldThrowIllegalArgumentExceptionForInvalidQuery()
          throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

    assertThrows(
        IllegalArgumentException.class, () -> fixture.findUsersByDisplayNamePartial(null, null));
  }

  @Test
  public void testFindUsersByDisplayNamePartial_blackbox_shouldReturnExpectedResults()
      throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

    PagerParams pagerParams = new PagerParams(20, 40);
    PaginatedList<User> resultFromDao = new PaginatedList<User>();
    resultFromDao.setTotalResults(1);
    resultFromDao.setPagerParams(pagerParams);
    resultFromDao.setItems(new LinkedList<User>());
    resultFromDao.getItems().add(UserFactory.createNewUserTemplate());
    when(fixture.getUserDao().findUserByDisplayNamePartial("asd", pagerParams))
        .thenReturn(resultFromDao);

    PaginatedList<User> results = fixture.findUsersByDisplayNamePartial("asd", pagerParams);

    assertNotNull(results);
    assertNotNull(results.getPagerParams());

    assertEquals(pagerParams.getOffset(), results.getPagerParams().getOffset());
    assertEquals(pagerParams.getMax(), results.getPagerParams().getMax());
    assertEquals(1, results.getTotalResults());
  }

  @Test
  public void testFindUsersByDisplayNamePartial_whitebox_expectUserServiceUnexpectedException()
      throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    assertThrows(
        UserServiceUnexpectedException.class,
        () ->
            fixture.findUsersByDisplayNamePartial(
                UserFactory.EXISTENT_USER, new PagerParams(20, 40)));
  }

  @Test
  public void testUpdateUser_defensive_iaeOnInvalidArgument() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.updateUser(null));
  }

  @Test
  public void testUpdateUser_defensive_iaeOnInvalidUserInfo() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    assertThrows(
        IllegalArgumentException.class,
        () -> fixture.updateUser(UserFactory.createNewUserTemplate()));
  }

  @Test
  public void testUpdateUser_blackbox_fveOnFailedValidation() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    User user = UserFactory.createExistingUser();
    user.setEmail("asdasd");
    when(fixture.getUserDao().updateUser(any(User.class))).thenReturn(true);
    assertThrows(ValidationException.class, () -> fixture.updateUser(user));
  }

  @Test
  public void testUpdateUser_blackbox_expectFieldValidationExceptionOnDuplicateUser()
      throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    assertThrows(
        ValidationException.class, () -> fixture.updateUser(UserFactory.createDuplicateUser()));
  }

  @Test
  public void testUpdateUser_whitebox_usueOnUnexexpectedException() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    doThrow(new IllegalArgumentException("test exception"))
        .when(fixture.getUserDao())
        .updateUser(any(User.class));
    assertThrows(
        UserServiceUnexpectedException.class,
        () -> fixture.updateUser(UserFactory.createExistingUser()));
  }

  @Test
  public void testUpdateUser_blackbox_unfeIfUserIsNotFound() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    when(fixture.getUserDao().updateUser(any(User.class))).thenReturn(false);
    assertThrows(
        UserNotFoundException.class, () -> fixture.updateUser(UserFactory.createExistingUser()));
  }

  @Test
  public void testUpdateUser_blackbox_shouldPassForNormalExecution() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    when(fixture.getUserDao().updateUser(any(User.class))).thenReturn(true);
    fixture.updateUser(UserFactory.createExistingUser());
  }

  @Test
  public void testDeleteUserByUuid_shouldThrowIaeOnNull() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.deleteUserByUuid(null));
  }

  @Test
  public void testDeleteUserByUuid_shouldThrowIaeOnEmpty() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    assertThrows(IllegalArgumentException.class, () -> fixture.deleteUserByUuid(""));
  }

  @Test
  public void testDeleteUserByUuid_shouldThrowUsersServiceUnexpectedExceptionOnUnexpected()
      throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    doThrow(new IllegalArgumentException("test exception"))
        .when(fixture.getUserDao())
        .findUserByUuid(any(String.class));
    assertThrows(UserServiceUnexpectedException.class, () -> fixture.deleteUserByUuid("asdasdasd"));
  }

  @Test
  public void testDeleteUserByUuid_shouldThrowUserNotFoundException() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    when(fixture.getUserDao().deleteUser(any(String.class))).thenReturn(false);
    when(fixture.getUserDao().findUserByUuid(any(String.class))).thenReturn(null);
    assertThrows(UserNotFoundException.class, () -> fixture.deleteUserByUuid("asdasdasd"));
  }

  @Test
  public void testDeleteUserByUuid_shouldBeOk() throws Exception {
    UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
    when(fixture.getUserDao().deleteUser(any(String.class))).thenReturn(true);
    when(fixture.getUserDao().findUserByUuid(any(String.class)))
        .thenReturn(UserFactory.createNewUserTemplate());
    fixture.deleteUserByUuid("asdasdasd");
  }
}
