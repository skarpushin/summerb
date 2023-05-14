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

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.StringIdGenerator;
import org.summerb.easycrud.api.dto.EntityChangedEvent;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.easycrud.impl.StringIdGeneratorUuidImpl;
import org.summerb.users.api.UserService;
import org.summerb.users.api.dto.User;
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.users.api.exceptions.UserServiceUnexpectedException;
import org.summerb.users.api.validation.DuplicateUserValidationError;
import org.summerb.users.impl.dao.UserDao;
import org.summerb.validation.ValidationContext;
import org.summerb.validation.ValidationContextFactory;
import org.summerb.validation.ValidationException;
import org.summerb.validation.errors.MustBeValidEmail;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

public class UserServiceImpl implements UserService {
  private UserDao userDao;
  private EventBus eventBus;
  private StringIdGenerator stringIdGenerator = new StringIdGeneratorUuidImpl();
  @Autowired private ValidationContextFactory validationContextFactory;

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public User createUser(User userTemplate) throws ValidationException {
    Preconditions.checkArgument(userTemplate != null, "userTemplate is required");

    validateUser(userTemplate);

    User userToCreate = new User();
    userToCreate.setDisplayName(userTemplate.getDisplayName());
    userToCreate.setEmail(userTemplate.getEmail());
    userToCreate.setIntegrationData(userTemplate.getIntegrationData());
    userToCreate.setIsBlocked(userTemplate.getIsBlocked());
    userToCreate.setLocale(userTemplate.getLocale());
    userToCreate.setRegisteredAt(userTemplate.getRegisteredAt());
    userToCreate.setTimeZone(userTemplate.getTimeZone());

    // Patch data
    if (userTemplate.getUuid() != null) {
      Preconditions.checkArgument(
          stringIdGenerator.isValidId(userTemplate.getUuid()), "User id is invalid");
      userToCreate.setUuid(userTemplate.getUuid());
    } else {
      userToCreate.setUuid(stringIdGenerator.generateNewId(userTemplate));
    }
    if (userToCreate.getRegisteredAt() == 0) {
      userToCreate.setRegisteredAt(new Date().getTime());
    }
    if (userToCreate.getLocale() == null) {
      userToCreate.setLocale(LocaleContextHolder.getLocale().toString());
    }
    if (userToCreate.getTimeZone() == null) {
      userToCreate.setTimeZone(Calendar.getInstance().getTimeZone().getID());
    }

    // Create
    try {
      userDao.createUser(userToCreate);
      eventBus.post(EntityChangedEvent.added(userToCreate));
    } catch (DuplicateKeyException dke) {
      throw new ValidationException(new DuplicateUserValidationError(User.FN_EMAIL));
    } catch (Throwable t) {
      String msg = String.format("Failed to create user with email '%s'", userToCreate.getEmail());
      throw new UserServiceUnexpectedException(msg, t);
    }

    return userToCreate;
  }

  private void validateUser(User user) throws ValidationException {
    var ctx = validationContextFactory.buildFor(user);

    ctx.validEmail(User::getEmail);
    ctx.lengthLe(User::getEmail, User.FN_EMAIL_SIZE);
    ctx.lengthLe(User::getDisplayName, User.FN_DISPLAY_NAME_SIZE);

    ctx.throwIfHasErrors();
  }

  @Override
  public User getUserByUuid(String userUuid) throws UserNotFoundException {
    Assert.hasText(userUuid, "userUuid must be provided");

    User foundUser;
    try {
      foundUser = userDao.findUserByUuid(userUuid);
    } catch (Throwable t) {
      String msg = String.format("Failed to find user '%s'", userUuid);
      throw new UserServiceUnexpectedException(msg, t);
    }

    if (foundUser == null) {
      throw new UserNotFoundException(userUuid);
    }

    return foundUser;
  }

  @Override
  public User getUserByEmail(String userEmail) throws ValidationException, UserNotFoundException {
    Assert.notNull(userEmail, "user email must be provided");
    if (!ValidationContext.isValidEmail(userEmail)) {
      throw new ValidationException(new MustBeValidEmail("email"));
    }

    User foundUser;
    try {
      foundUser = userDao.findUserByEmail(userEmail);
    } catch (Throwable t) {
      String msg = String.format("Failed to find user by email '%s'", userEmail);
      throw new UserServiceUnexpectedException(msg, t);
    }

    if (foundUser == null) {
      throw new UserNotFoundException(userEmail);
    }

    return foundUser;
  }

  @Override
  public PaginatedList<User> findUsersByDisplayNamePartial(
      String displayNamePartial, PagerParams pagerParams) throws ValidationException {
    Preconditions.checkArgument(
        StringUtils.hasText(displayNamePartial), "Query text must be specified");

    try {
      return userDao.findUserByDisplayNamePartial(displayNamePartial, pagerParams);
    } catch (Throwable t) {
      String msg =
          String.format("Failed to search users by partial user name '%s'", displayNamePartial);
      throw new UserServiceUnexpectedException(msg, t);
    }
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void updateUser(User user) throws ValidationException, UserNotFoundException {
    Preconditions.checkArgument(user != null, "User reference required");
    Preconditions.checkArgument(StringUtils.hasText(user.getUuid()), "User uuid must be provided");

    validateUser(user);

    boolean isUpdatedSuccessfully;
    try {
      isUpdatedSuccessfully = userDao.updateUser(user);
      eventBus.post(EntityChangedEvent.updated(user));
    } catch (DuplicateKeyException dke) {
      throw new ValidationException(new DuplicateUserValidationError(User.FN_EMAIL));
    } catch (Throwable t) {
      String msg = String.format("Failed to update user '%s'", user.getUuid());
      throw new UserServiceUnexpectedException(msg, t);
    }

    if (!isUpdatedSuccessfully) {
      throw new UserNotFoundException(user.getUuid());
    }
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void deleteUserByUuid(String userUuid) throws UserNotFoundException {
    Preconditions.checkArgument(userUuid != null, "User uuid required");
    Preconditions.checkArgument(StringUtils.hasText(userUuid), "User uuid must be provided");

    boolean isDeletedSucceessfully = false;
    try {
      User userToDelete = userDao.findUserByUuid(userUuid);

      if (userToDelete != null) {
        isDeletedSucceessfully = userDao.deleteUser(userUuid);
        // NOTE: Assumed, that all related stuff will be deleted
        // automatically using CASCADE DELETE in the database
        eventBus.post(EntityChangedEvent.removedObject(userToDelete));
      }
    } catch (Throwable t) {
      String msg = String.format("Failed to delete user '%s'", userUuid);
      throw new UserServiceUnexpectedException(msg, t);
    }

    if (!isDeletedSucceessfully) {
      throw new UserNotFoundException(userUuid);
    }
  }

  public UserDao getUserDao() {
    return userDao;
  }

  public void setUserDao(UserDao userDao) {
    this.userDao = userDao;
  }

  public EventBus getEventBus() {
    return eventBus;
  }

  @Required
  public void setEventBus(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  public StringIdGenerator getStringIdGenerator() {
    return stringIdGenerator;
  }

  @Autowired(required = false)
  public void setStringIdGenerator(StringIdGenerator stringIdGenerator) {
    this.stringIdGenerator = stringIdGenerator;
  }
}
