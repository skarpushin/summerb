/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.users.api.dto.User;
import org.summerb.users.api.dto.UserFactory;
import org.summerb.users.impl.dao.UserDao;

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
