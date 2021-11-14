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
package org.summerb.webappboilerplate.security.impls;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.summerb.users.api.AuthTokenService;
import org.summerb.users.api.UserService;
import org.summerb.users.api.dto.AuthToken;
import org.summerb.users.api.dto.User;
import org.summerb.webappboilerplate.utils.CurrentRequestUtils;

/**
 * This impl simply delegates all queries to summerb's AuthTokenService
 * 
 * @author skarpushin
 * 
 */
public class PersistentTokenRepositoryDefaultImpl implements PersistentTokenRepository {
	private Logger log = LogManager.getLogger(getClass());

	private AuthTokenService authTokenService;
	private UserService userService;

	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		try {
			authTokenService.createAuthToken(token.getUsername(), CurrentRequestUtils.get().getRemoteAddr(),
					token.getSeries(), token.getTokenValue());
		} catch (Throwable e) {
			throw new RuntimeException("Failed to create auth token", e);
		}
	}

	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		try {
			authTokenService.updateToken(series, lastUsed.getTime(), tokenValue);
		} catch (Throwable e) {
			throw new RuntimeException("Failed to update auth token", e);
		}
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		try {
			AuthToken authToken = authTokenService.getAuthTokenByUuid(seriesId);
			User user = userService.getUserByUuid(authToken.getUserUuid());

			return new PersistentRememberMeToken(user.getEmail(), authToken.getUuid(), authToken.getTokenValue(),
					new Date(authToken.getLastVerifiedAt()));
		} catch (Throwable e) {
			log.info("Persistent auth token wasn't found for seriesId " + seriesId);
			return null;
		}
	}

	@Override
	public void removeUserTokens(String username) {
		try {
			User user = userService.getUserByEmail(username);
			List<AuthToken> authTokens = authTokenService.findUserAuthTokens(user.getUuid());
			for (AuthToken authToken : authTokens) {
				authTokenService.deleteAuthToken(authToken.getUuid());
			}
		} catch (Throwable e) {
			throw new RuntimeException("Failed to delete user auth tokens", e);
		}
	}

	public AuthTokenService getAuthTokenService() {
		return authTokenService;
	}

	@Autowired
	public void setAuthTokenService(AuthTokenService authTokenService) {
		this.authTokenService = authTokenService;
	}

	public UserService getUserService() {
		return userService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
