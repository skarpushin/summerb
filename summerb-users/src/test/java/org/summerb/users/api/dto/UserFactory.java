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
package org.summerb.users.api.dto;

import java.util.Date;

public class UserFactory {
	public static final String EXISTENT_USER = "userExistent";
	public static final String EXISTENT_USER_2_PROBLEM_WITH_PASSWORD = "userExistent2";
	public static final String EXISTENT_USER_EMAIL = "exsitent@aaa.ru";

	public static final String NON_EXISTENT_USER = "userNonExistent";
	public static final String NON_EXISTENT_USER_EMAIL = "nonExistent@aaa.ru";

	public static final String USER_RESULT_IN_EXCEPTION = "userWithUnexpectedException";
	public static final String USER_EMAIL_RESULT_IN_EXCEPTION = "exception@aaa.ru";

	public static final String EXISTENT_USER_WITH_MISSING_PASSWORD = "userWithMissingPassword";
	public static final String EXISTENT_USER_WITH_EXPIRED_TOKEN = "userWithExpiredToken";
	private static User duplicateUser;

	private UserFactory() {
	}

	public static User createNewUserTemplate() {
		User ret = new User();
		ret.setDisplayName("Display name");
		ret.setEmail(UserFactory.EXISTENT_USER_EMAIL);
		ret.setRegisteredAt(new Date().getTime());
		ret.setLocale("ru_RU");
		ret.setTimeZone("GMT+4");
		return ret;
	}

	public static User createExistingUser() {
		User ret = createNewUserTemplate();
		ret.setUuid(EXISTENT_USER);
		return ret;
	}

	public static User createExistingUser2() {
		User ret = createNewUserTemplate();
		ret.setUuid(EXISTENT_USER_2_PROBLEM_WITH_PASSWORD);
		return ret;
	}

	public static User createUserWithMissingPassword() {
		User ret = createNewUserTemplate();
		ret.setUuid(EXISTENT_USER_WITH_MISSING_PASSWORD);
		return ret;
	}

	public static User createUserWithExpiredToken() {
		User ret = createNewUserTemplate();
		ret.setUuid(EXISTENT_USER_WITH_EXPIRED_TOKEN);
		return ret;
	}

	public static User createDuplicateUser() {
		if (duplicateUser == null) {
			duplicateUser = createNewUserTemplate();
			duplicateUser.setUuid(EXISTENT_USER_WITH_EXPIRED_TOKEN);
		}
		return duplicateUser;
	}
}
