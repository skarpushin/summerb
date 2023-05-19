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

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.summerb.users.api.AuthTokenService;
import org.summerb.users.api.PasswordService;
import org.summerb.users.api.UserService;
import org.summerb.users.api.dto.AuthToken;
import org.summerb.users.api.dto.User;
import org.summerb.users.api.exceptions.AuthTokenNotFoundException;
import org.summerb.users.api.exceptions.InvalidPasswordException;
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.users.api.exceptions.UserServiceUnexpectedException;
import org.summerb.users.impl.dao.AuthTokenDao;
import org.summerb.validation.ValidationError;
import org.summerb.validation.ValidationException;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

public class AuthTokenServiceImpl implements AuthTokenService {
  // protected static Logger log =
  // Logger.getLogger(AuthTokenServiceImpl.class);

  protected UserService userService;
  protected PasswordService passwordService;
  protected AuthTokenDao authTokenDao;
  /**
   * How long token may live even if it's frequently used. After that period of time token will be
   * deleted and user will have to re-login
   *
   * <p>14 days by default
   */
  protected long authTokenTimeToLiveSeconds = 60 * 60 * 24 * 14;

  public AuthTokenServiceImpl(
      AuthTokenDao authTokenDao, UserService userService, PasswordService passwordService) {
    Preconditions.checkArgument(authTokenDao != null, "authTokenDao required");
    Preconditions.checkArgument(userService != null, "userService required");
    Preconditions.checkArgument(passwordService != null, "passwordService required");

    this.authTokenDao = authTokenDao;
    this.userService = userService;
    this.passwordService = passwordService;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public AuthToken authenticate(String userEmail, String passwordPlain, String clientIp)
      throws UserNotFoundException, ValidationException, InvalidPasswordException {
    Preconditions.checkArgument(userEmail != null);
    Preconditions.checkArgument(passwordPlain != null);
    Preconditions.checkArgument(clientIp != null);

    try {
      User user = validateAndGetUser(userEmail, passwordPlain);
      return createAuthToken(
          user.getEmail(), clientIp, UUID.randomUUID().toString(), UUID.randomUUID().toString());
    } catch (Throwable t) {
      Throwables.throwIfInstanceOf(t, UserNotFoundException.class);
      Throwables.throwIfInstanceOf(t, ValidationException.class);
      Throwables.throwIfInstanceOf(t, InvalidPasswordException.class);

      String msg = String.format("Failed to create auth otken for user '%s'", userEmail);
      throw new UserServiceUnexpectedException(msg, t);
    }
  }

  protected User validateAndGetUser(String userEmail, String passwordPlain)
      throws UserNotFoundException, ValidationException, InvalidPasswordException {
    try {
      User user = userService.getUserByEmail(userEmail);

      boolean isPasswordValid = passwordService.isUserPasswordValid(user.getUuid(), passwordPlain);
      if (!isPasswordValid) {
        throw new InvalidPasswordException();
      }

      return user;
    } catch (Throwable t) {
      Throwables.throwIfInstanceOf(t, UserNotFoundException.class);
      Throwables.throwIfInstanceOf(t, ValidationException.class);
      Throwables.throwIfInstanceOf(t, InvalidPasswordException.class);

      String msg =
          String.format("Failed to validate user '%s' and password '%s'", userEmail, passwordPlain);
      throw new UserServiceUnexpectedException(msg, t);
    }
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public AuthToken createAuthToken(
      String userEmail, String clientIp, String tokenUuid, String tokenValueUuid)
      throws UserNotFoundException, ValidationException {
    Preconditions.checkArgument(userEmail != null);
    Preconditions.checkArgument(clientIp != null);
    Preconditions.checkArgument(StringUtils.hasText(tokenUuid));
    Preconditions.checkArgument(StringUtils.hasText(tokenValueUuid));

    try {
      User user = userService.getUserByEmail(userEmail);
      AuthToken authToken = buildNewAuthToken(user, clientIp, tokenUuid, tokenValueUuid);
      authTokenDao.createAuthToken(authToken);
      return authToken;
    } catch (Throwable t) {
      Throwables.throwIfInstanceOf(t, UserNotFoundException.class);
      Throwables.throwIfInstanceOf(t, ValidationException.class);

      String msg = String.format("Failed to create auth otken for user '%s'", userEmail);
      throw new UserServiceUnexpectedException(msg, t);
    }
  }

  protected AuthToken buildNewAuthToken(
      User user, String clientIp, String tokenUuid, String tokenValueUuid) {
    long now = getNow();
    AuthToken ret = new AuthToken();
    ret.setClientIp(clientIp);
    ret.setCreatedAt(now);
    ret.setExpiresAt(calculateAuthTokenExpirationPoint(now));
    ret.setLastVerifiedAt(now);
    ret.setUserUuid(user.getUuid());
    ret.setUuid(tokenUuid);
    ret.setTokenValue(tokenValueUuid);
    return ret;
  }

  protected long calculateAuthTokenExpirationPoint(long now) {
    return now + authTokenTimeToLiveSeconds * 1000;
  }

  // TODO: Use NowResolver
  protected long getNow() {
    return new Date().getTime();
  }

  @Override
  public AuthToken getAuthTokenByUuid(String authTokenUuid) throws AuthTokenNotFoundException {
    Preconditions.checkArgument(authTokenUuid != null);

    try {
      AuthToken ret = authTokenDao.findAuthTokenByUuid(authTokenUuid);
      if (ret == null) {
        throw new AuthTokenNotFoundException();
      }
      return ret;
    } catch (Throwable t) {
      Throwables.throwIfInstanceOf(t, AuthTokenNotFoundException.class);

      String msg = String.format("Failed to get auth token by id '%s'", authTokenUuid);
      throw new UserServiceUnexpectedException(msg, t);
    }
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public AuthToken isAuthTokenValid(String userUuid, String authTokenUuid, String tokenValue)
      throws UserNotFoundException {
    Preconditions.checkArgument(userUuid != null);
    Preconditions.checkArgument(authTokenUuid != null);
    Preconditions.checkArgument(StringUtils.hasText(tokenValue), "TokenValue is mandatory");

    try {
      // First - check token itself
      AuthToken authToken = getAuthTokenByUuid(authTokenUuid);
      if (authToken.getExpiresAt() < getNow()) {
        authTokenDao.deleteAuthToken(authTokenUuid);
        return null;
      }

      if (!tokenValue.equals(authToken.getTokenValue())) {
        return null;
      }

      // Check reference to user
      User user = userService.getUserByUuid(userUuid);
      if (!authToken.getUserUuid().equals(user.getUuid())) {
        return null;
      }

      // Now we need to update time when token was checked
      authToken.setTokenValue(UUID.randomUUID().toString());
      authToken.setLastVerifiedAt(getNow());
      authTokenDao.updateToken(
          authTokenUuid, authToken.getLastVerifiedAt(), authToken.getTokenValue());

      return authToken;
    } catch (AuthTokenNotFoundException nfe) {
      return null;
    } catch (Throwable t) {
      Throwables.throwIfInstanceOf(t, UserNotFoundException.class);

      String msg =
          String.format(
              "Failed to check auth token '%s' validity for user '%s'", authTokenUuid, userUuid);
      throw new UserServiceUnexpectedException(msg, t);
    }
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void updateToken(String authTokenUuid, long lastVerifiedAt, String newTokenValue)
      throws AuthTokenNotFoundException, ValidationException {
    Preconditions.checkArgument(authTokenUuid != null);
    Preconditions.checkArgument(StringUtils.hasText(newTokenValue), "TokenValue is mandatory");

    try {
      // First - check token itself
      AuthToken authToken = getAuthTokenByUuid(authTokenUuid);
      if (newTokenValue.equals(authToken.getTokenValue())) {
        throw new ValidationException(
            new ValidationError("newTokenValue", "validation.newValueExpected"));
      }

      // Now we need to update time when token was checked
      authTokenDao.updateToken(authTokenUuid, lastVerifiedAt, newTokenValue);
    } catch (Throwable t) {
      Throwables.throwIfInstanceOf(t, ValidationException.class);
      Throwables.throwIfInstanceOf(t, AuthTokenNotFoundException.class);

      String msg = String.format("Failed to update token '%s'", authTokenUuid);
      throw new UserServiceUnexpectedException(msg, t);
    }
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void deleteAuthToken(String authTokenUuid) throws AuthTokenNotFoundException {
    Preconditions.checkArgument(authTokenUuid != null);

    try {
      getAuthTokenByUuid(authTokenUuid);
      authTokenDao.deleteAuthToken(authTokenUuid);
    } catch (AuthTokenNotFoundException nfe) {
      // it's ok
      return;
    } catch (Throwable t) {
      String msg = String.format("Failed to delete auth token '%s'", authTokenUuid);
      throw new UserServiceUnexpectedException(msg, t);
    }
  }

  @Override
  public List<AuthToken> findUserAuthTokens(String userUuid) throws UserNotFoundException {
    Preconditions.checkArgument(StringUtils.hasText(userUuid));

    try {
      User user = userService.getUserByUuid(userUuid);
      return authTokenDao.findAuthTokensByUser(user.getUuid());
    } catch (Throwable t) {
      Throwables.throwIfInstanceOf(t, UserNotFoundException.class);

      String msg = String.format("Failed to find user '%s' authtokens", userUuid);
      throw new UserServiceUnexpectedException(msg, t);
    }
  }

  public UserService getUserService() {
    return userService;
  }

  public PasswordService getPasswordService() {
    return passwordService;
  }

  public AuthTokenDao getAuthTokenDao() {
    return authTokenDao;
  }

  public long getAuthTokenTimeToLiveSeconds() {
    return authTokenTimeToLiveSeconds;
  }

  public void setAuthTokenTimeToLiveSeconds(long authTokenTimeToLiveSeconds) {
    this.authTokenTimeToLiveSeconds = authTokenTimeToLiveSeconds;
  }
}
