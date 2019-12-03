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
package org.summerb.users.api.dto;

import java.util.Date;

public class AuthTokenFactory {
	public static final String AUTH_TOKEN_EXCEPTION = "exception";
	public static final String AUTH_TOKEN_EXISTENT = "authTokenExistentOne";
	public static final String AUTH_TOKEN_EXISTENT_VALUE = "authTokenExistentOneValue";
	public static final String AUTH_TOKEN_EXPIRED = "authTokenExpired";
	public static final String AUTH_TOKEN_NOT_EXISTENT = "authTokenNoneExistentOne";

	public static AuthToken createAuthTokenForExistentUser() {
		AuthToken ret = new AuthToken();
		ret.setClientIp("1.1.1.1");
		ret.setCreatedAt(new Date().getTime() - 10000);
		ret.setExpiresAt(new Date().getTime() + 3600000);
		ret.setLastVerifiedAt(new Date().getTime());
		ret.setUserUuid(UserFactory.EXISTENT_USER);
		ret.setUuid(AUTH_TOKEN_EXISTENT);
		ret.setTokenValue(AUTH_TOKEN_EXISTENT_VALUE);
		return ret;
	}

	public static AuthToken createExpiredAuthToken() {
		AuthToken ret = new AuthToken();
		ret.setClientIp("1.1.1.1");
		ret.setCreatedAt(new Date().getTime() - 1000000);
		ret.setExpiresAt(new Date().getTime() - 100000);
		ret.setLastVerifiedAt(new Date().getTime() - 200000);
		ret.setUserUuid(UserFactory.EXISTENT_USER_WITH_EXPIRED_TOKEN);
		ret.setUuid(AUTH_TOKEN_EXPIRED);
		return ret;
	}
}
