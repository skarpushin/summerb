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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.LinkedList;

import org.junit.Test;
import org.springframework.dao.DuplicateKeyException;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.users.api.dto.User;
import org.summerb.users.api.dto.UserFactory;
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.users.api.exceptions.UserServiceUnexpectedException;
import org.summerb.validation.FieldValidationException;

/**
 * NOTE: These tests does not follow test names convention
 */
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
	public void testCreateUser_whitebox_expectReturnedValueIsNotTheSameReferenceAsParameter() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

		User template = UserFactory.createNewUserTemplate();
		User result = fixture.createUser(template);

		// add additional test code here
		assertNotNull(result);
		assertNotSame(template, result);
	}

	@Test(expected = FieldValidationException.class)
	public void testCreateUser_blackbox_expectValidationExceptionOnInvalidEmail() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

		User user = UserFactory.createNewUserTemplate();
		user.setEmail("abara-cadabara");

		fixture.createUser(user);

		fail();
	}

	@Test(expected = FieldValidationException.class)
	public void testCreateUser_blackbox_expectValidationExceptionOnInvalidEmail1() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

		User user = UserFactory.createNewUserTemplate();
		user.setEmail(null);

		fixture.createUser(user);

		fail();
	}

	@Test(expected = FieldValidationException.class)
	public void testCreateUser_whitebox_expectDuplicateUserException() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

		User user = UserFactory.createNewUserTemplate();
		doThrow(new DuplicateKeyException("duplicate user")).when(fixture.getUserDao()).createUser(any(User.class));

		fixture.createUser(user);

		fail();
	}

	@Test(expected = UserServiceUnexpectedException.class)
	public void testCreateUser_whitebox_expectOurExceptionInsteadOfUnexpectedOne() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

		User user = UserFactory.createNewUserTemplate();
		doThrow(new IllegalArgumentException("test exception")).when(fixture.getUserDao()).createUser(any(User.class));

		fixture.createUser(user);

		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUser_defensive_expectIllegalArgumentExceptionInsteadOfNpe() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

		fixture.createUser(null);

		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetUserByUuid_defensive_expectIllegalArgumentExceptionInsteadOfNpe1() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		fixture.getUserByUuid(null);
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetUserByUuid_defensive_expectIllegalArgumentExceptionInsteadOfNpe2() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		fixture.getUserByUuid("");
		fail();
	}

	@Test(expected = UserServiceUnexpectedException.class)
	public void testGetUserByUuid_whitebox_expectOurExceptionInsteadOfUnexpectedOne() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

		doThrow(new IllegalArgumentException("test exception")).when(fixture.getUserDao())
				.findUserByUuid(any(String.class));

		fixture.getUserByUuid("aaa");

		fail();
	}

	@Test(expected = UserNotFoundException.class)
	public void testGetUserByUuid_blackbox_expectUserNotFoundException() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

		when(fixture.getUserDao().findUserByUuid(any(String.class))).thenReturn(null);

		fixture.getUserByUuid("aaa");

		fail();
	}

	@Test
	public void testGetUserByUuid_blackbox_expectUserWasFound() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

		when(fixture.getUserDao().findUserByUuid(any(String.class))).thenReturn(UserFactory.createNewUserTemplate());

		assertNotNull(fixture.getUserByUuid("aaa"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetUserByEmail_defensive_expectIllegalArgumentExceptionOnNull() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		fixture.getUserByEmail(null);
		fail();
	}

	@Test(expected = FieldValidationException.class)
	public void testGetUserByEmail_blackbox_expectFieldValidationExceptionOnEmptyString() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		fixture.getUserByEmail("");
		fail();
	}

	@Test(expected = FieldValidationException.class)
	public void testGetUserByEmail_blackbox_expectFieldValidationExceptionOnWrongFormat() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		fixture.getUserByEmail("abara-cadabara");
		fail();
	}

	@Test(expected = UserServiceUnexpectedException.class)
	public void testGetUserByEmail_whitebox_expectOurExceptionOnUnexpectedOne() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		doThrow(new IllegalArgumentException("test exception")).when(fixture.getUserDao())
				.findUserByEmail(any(String.class));

		fixture.getUserByEmail(UserFactory.createNewUserTemplate().getEmail());
		fail();
	}

	@Test(expected = UserNotFoundException.class)
	public void testGetUserByEmail_blackbox_expectUserNotFoundException() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

		when(fixture.getUserDao().findUserByEmail(any(String.class))).thenReturn(null);

		fixture.getUserByEmail(UserFactory.createNewUserTemplate().getEmail());
		fail();
	}

	@Test
	public void testGetUserByEmail_blackbox_expectFoundUser() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

		when(fixture.getUserDao().findUserByEmail(any(String.class))).thenReturn(UserFactory.createNewUserTemplate());
		User user = fixture.getUserByEmail(UserFactory.createNewUserTemplate().getEmail());

		assertNotNull(user);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindUsersByDisplayNamePartial_defensive_shouldThrowIllegalArgumentExceptionForInvalidQuery()
			throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

		fixture.findUsersByDisplayNamePartial(null, null);
		fail();
	}

	@Test
	public void testFindUsersByDisplayNamePartial_blackbox_shouldReturnExpectedResults() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();

		PagerParams pagerParams = new PagerParams(20, 40);
		PaginatedList<User> resultFromDao = new PaginatedList<User>();
		resultFromDao.setTotalResults(1);
		resultFromDao.setPagerParams(pagerParams);
		resultFromDao.setItems(new LinkedList<User>());
		resultFromDao.getItems().add(UserFactory.createNewUserTemplate());
		when(fixture.getUserDao().findUserByDisplayNamePartial("asd", pagerParams)).thenReturn(resultFromDao);

		PaginatedList<User> results = fixture.findUsersByDisplayNamePartial("asd", pagerParams);

		assertNotNull(results);
		assertNotNull(results.getPagerParams());

		assertEquals(pagerParams.getOffset(), results.getPagerParams().getOffset());
		assertEquals(pagerParams.getMax(), results.getPagerParams().getMax());
		assertEquals(1, results.getTotalResults());
	}

	@Test(expected = UserServiceUnexpectedException.class)
	public void testFindUsersByDisplayNamePartial_whitebox_expectUserServiceUnexpectedException() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		fixture.findUsersByDisplayNamePartial(UserFactory.EXISTENT_USER, new PagerParams(20, 40));
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateUser_defensive_iaeOnInvalidArgument() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		fixture.updateUser(null);
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateUser_defensive_iaeOnInvalidUserInfo() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		fixture.updateUser(UserFactory.createNewUserTemplate());
		fail();
	}

	@Test(expected = FieldValidationException.class)
	public void testUpdateUser_blackbox_fveOnFailedValidation() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		User user = UserFactory.createExistingUser();
		user.setEmail("asdasd");
		when(fixture.getUserDao().updateUser(any(User.class))).thenReturn(true);
		fixture.updateUser(user);
		fail();
	}

	@Test(expected = FieldValidationException.class)
	public void testUpdateUser_blackbox_expectFieldValidationExceptionOnDuplicateUser() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		fixture.updateUser(UserFactory.createDuplicateUser());
		fail();
	}

	@Test(expected = UserServiceUnexpectedException.class)
	public void testUpdateUser_whitebox_usueOnUnexexpectedException() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		doThrow(new IllegalArgumentException("test exception")).when(fixture.getUserDao()).updateUser(any(User.class));
		fixture.updateUser(UserFactory.createExistingUser());
		fail();
	}

	@Test(expected = UserNotFoundException.class)
	public void testUpdateUser_blackbox_unfeIfUserIsNotFound() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		when(fixture.getUserDao().updateUser(any(User.class))).thenReturn(false);
		fixture.updateUser(UserFactory.createExistingUser());
		fail();
	}

	@Test
	public void testUpdateUser_blackbox_shouldPassForNormalExecution() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		when(fixture.getUserDao().updateUser(any(User.class))).thenReturn(true);
		fixture.updateUser(UserFactory.createExistingUser());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteUserByUuid_shouldThrowIaeOnNull() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		fixture.deleteUserByUuid(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteUserByUuid_shouldThrowIaeOnEmpty() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		fixture.deleteUserByUuid("");
	}

	@Test(expected = UserServiceUnexpectedException.class)
	public void testDeleteUserByUuid_shouldThrowUsersServiceUnexpectedExceptionOnUnexpected() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		doThrow(new IllegalArgumentException("test exception")).when(fixture.getUserDao())
				.findUserByUuid(any(String.class));
		fixture.deleteUserByUuid("asdasdasd");
	}

	@Test(expected = UserNotFoundException.class)
	public void testDeleteUserByUuid_shouldThrowUserNotFoundException() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		when(fixture.getUserDao().deleteUser(any(String.class))).thenReturn(false);
		when(fixture.getUserDao().findUserByUuid(any(String.class))).thenReturn(null);
		fixture.deleteUserByUuid("asdasdasd");
	}

	@Test
	public void testDeleteUserByUuid_shouldBeOk() throws Exception {
		UserServiceImpl fixture = UserServiceImplFactory.createUsersServiceImpl();
		when(fixture.getUserDao().deleteUser(any(String.class))).thenReturn(true);
		when(fixture.getUserDao().findUserByUuid(any(String.class))).thenReturn(UserFactory.createNewUserTemplate());
		fixture.deleteUserByUuid("asdasdasd");
	}

}
