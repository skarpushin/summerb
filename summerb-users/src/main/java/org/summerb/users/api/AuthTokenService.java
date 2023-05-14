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
package org.summerb.users.api;

import java.util.List;

import org.summerb.users.api.dto.AuthToken;
import org.summerb.users.api.exceptions.AuthTokenNotFoundException;
import org.summerb.users.api.exceptions.InvalidPasswordException;
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.validation.ValidationException;

/**
 * This service is for managing user authentication tokens.
 * 
 * @author skarpushin
 * 
 */
public interface AuthTokenService {

	/**
	 * This is high-level auth method. It will verify credentials and return auth
	 * token if all ok. In underhood will call
	 * {@link #createAuthToken(String, String, String, String)}
	 * 
	 * @return created auth token
	 * @throws UserNotFoundException    if such user not found
	 * @throws ValidationException if email has wrong format
	 * @throws InvalidPasswordException if unable to verify password validity
	 */
	AuthToken authenticate(String userEmail, String passwordPlain, String clientIp)
			throws UserNotFoundException, ValidationException, InvalidPasswordException;

	/**
	 * Low-level method which will just create auth token without any
	 * chesk/validations
	 * 
	 * @return created auth token
	 * @throws UserNotFoundException    if such user not found
	 * @throws ValidationException if email has wrong format
	 */
	AuthToken createAuthToken(String userEmail, String clientIp, String tokenUuid, String tokenValueUuid)
			throws UserNotFoundException, ValidationException;

	AuthToken getAuthTokenByUuid(String authTokenUuid) throws AuthTokenNotFoundException;

	/**
	 * Verify token validity and if valid updated it with new values for tokenValue
	 * and lastUpdated.
	 * 
	 * This is a convenient all-in-one method. You don't have to use it if you want
	 * to implement custom token validation checks, don't forget to use
	 * {@link #updateToken(String, long, String)} to mitigate the "stolen token
	 * database" situation
	 * 
	 * @param userUuid      user which this auth token related to
	 * @param authTokenUuid auth token unique id
	 * @param tokenValue    auth token value (AuthTokenService will generate new
	 *                      value for this field after successful validation)
	 * @return new Auth token or null if token is not valid
	 * @throws UserNotFoundException if user not found
	 */
	AuthToken isAuthTokenValid(String userUuid, String authTokenUuid, String tokenValue) throws UserNotFoundException;

	/**
	 * Update token with new 2nd part security value
	 * 
	 * @param authTokenUuid  token uuid to update
	 * @param lastVerifiedAt when this token was last verified
	 * @param newTokenValue  new value for tokenValue field. It MUST NOT be equal to
	 *                       previous value
	 * @throws AuthTokenNotFoundException if auth token is not found
	 * @throws ValidationException   if newTokenValue = current tokenValue for
	 *                                    that token
	 */
	void updateToken(String authTokenUuid, long lastVerifiedAt, String newTokenValue)
			throws AuthTokenNotFoundException, ValidationException;

	void deleteAuthToken(String authTokenUuid) throws AuthTokenNotFoundException;

	/**
	 * Find all auth tokens for this user
	 * 
	 * @return non-null (might be empty) list
	 * @throws UserNotFoundException
	 */
	List<AuthToken> findUserAuthTokens(String userUuid) throws UserNotFoundException;
}
