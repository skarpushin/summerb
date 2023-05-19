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

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.summerb.users.api.PasswordService;
import org.summerb.users.api.UserService;
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.users.api.exceptions.UserServiceUnexpectedException;
import org.summerb.users.impl.dao.PasswordDao;
import org.summerb.users.impl.dom.Password;
import org.summerb.validation.ValidationException;
import org.summerb.validation.errors.MustNotBeNull;

import com.google.common.base.Preconditions;

public class PasswordServiceImpl implements PasswordService {
  protected static final String FN_PASSWORD = "password";
  protected UserService userService;
  protected PasswordDao passwordDao;
  protected PasswordEncoder passwordEncoder;

  public PasswordServiceImpl(
      PasswordDao passwordDao, PasswordEncoder passwordEncoder, UserService userService) {
    Preconditions.checkArgument(userService != null, "userService required");
    Preconditions.checkArgument(passwordDao != null, "passwordDao required");
    Preconditions.checkArgument(passwordEncoder != null, "passwordEncoder required");

    this.passwordDao = passwordDao;
    this.passwordEncoder = passwordEncoder;
    this.userService = userService;
  }

  @Override
  public boolean isUserPasswordValid(String userUuid, String passwordPlain)
      throws UserNotFoundException {
    Preconditions.checkArgument(userUuid != null);
    Preconditions.checkArgument(passwordPlain != null);
    assertUserExists(userUuid);

    try {
      Password password = passwordDao.findPasswordByUserUuid(userUuid);
      if (password == null) {
        return false;
      }

      if (!isPasswordMatch(passwordPlain, password.getPasswordHash())) {
        return false;
      }
    } catch (Throwable t) {
      throw new UserServiceUnexpectedException(
          String.format("Failed to validate user '%s' password", userUuid), t);
    }

    return true;
  }

  protected boolean isPasswordMatch(String providedPlainPassword, String expectedHash) {
    if (expectedHash == null) {
      return false;
    }
    return passwordEncoder.matches(providedPlainPassword, expectedHash);
  }

  protected void assertUserExists(String userUuid) throws UserNotFoundException {
    userService.getUserByUuid(userUuid);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void setUserPassword(String userUuid, String newPasswordPlain)
      throws UserNotFoundException, ValidationException {
    Preconditions.checkArgument(userUuid != null);
    Preconditions.checkArgument(newPasswordPlain != null);
    assertUserExists(userUuid);

    if (!StringUtils.hasText(newPasswordPlain)) {
      throw new ValidationException(new MustNotBeNull(FN_PASSWORD));
    }

    String newPasswordHash = null;
    try {
      newPasswordHash = encodePassword(newPasswordPlain);

      // sanity check
      if (!isPasswordMatch(newPasswordPlain, newPasswordHash)) {
        throw new RuntimeException("Password doesn't match just created hash");
      }

      // set user password
      int updateResult = passwordDao.updateUserPassword(userUuid, newPasswordHash);
      if (updateResult < 1) {
        throw new RuntimeException(
            "updateUserPassword returned unexpected result = " + updateResult);
      }
    } catch (Throwable t) {
      throw new UserServiceUnexpectedException(
          String.format("Failed to set user '%s' passwordHash '%s'", userUuid, newPasswordHash), t);
    }
  }

  protected String encodePassword(String newPasswordPlain) {
    return passwordEncoder.encode(newPasswordPlain);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public String getNewRestorationTokenForUser(String userUuid) throws UserNotFoundException {
    Preconditions.checkArgument(userUuid != null);
    assertUserExists(userUuid);

    try {
      String restorationToken = UUID.randomUUID().toString();

      int updateResult = passwordDao.setRestorationToken(userUuid, restorationToken);
      if (updateResult != 1) {
        throw new RuntimeException(
            "createRestorationToken returned unexpected result = " + updateResult);
      }

      return restorationToken;
    } catch (Throwable t) {
      throw new UserServiceUnexpectedException(
          String.format("Failed to create restoration token for user '%s'", userUuid), t);
    }
  }

  @Override
  public boolean isRestorationTokenValid(String userUuid, String restorationTokenUuid)
      throws UserNotFoundException {
    Preconditions.checkArgument(userUuid != null);
    Preconditions.checkArgument(restorationTokenUuid != null);
    assertUserExists(userUuid);

    try {
      Password password = passwordDao.findPasswordByUserUuid(userUuid);
      if (password == null || !restorationTokenUuid.equals(password.getRestorationToken())) {
        return false;
      }
    } catch (Throwable t) {
      throw new UserServiceUnexpectedException(
          String.format("Failed to check user '%s' restoration token validity", userUuid), t);
    }

    return true;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void deleteRestorationToken(String userUuid) throws UserNotFoundException {
    Preconditions.checkArgument(userUuid != null);
    assertUserExists(userUuid);

    try {
      int updateResult = passwordDao.setRestorationToken(userUuid, null);
      if (updateResult != 1) {
        throw new RuntimeException(
            "deleteRestorationToken returned unexpected result = " + updateResult);
      }
    } catch (Throwable t) {
      throw new UserServiceUnexpectedException(
          String.format("Failed to delete restoration token for user '%s'", userUuid), t);
    }
  }

  public UserService getUserService() {
    return userService;
  }

  public PasswordDao getPasswordDao() {
    return passwordDao;
  }

  public PasswordEncoder getPasswordEncoder() {
    return passwordEncoder;
  }
}
