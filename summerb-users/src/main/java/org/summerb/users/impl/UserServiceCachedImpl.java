/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.summerb.users.api.UserService;
import org.summerb.users.api.dto.User;
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.utils.cache.CachesInvalidationNeeded;
import org.summerb.utils.easycrud.api.dto.EntityChangedEvent;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;
import org.summerb.utils.tx.TransactionBoundCache;
import org.summerb.validation.ValidationException;

public class UserServiceCachedImpl implements UserService, InitializingBean {
  protected Logger log = LoggerFactory.getLogger(getClass());

  protected UserService userService;
  protected EventBus eventBus;

  protected LoadingCache<String, User> cacheByEmail;
  protected LoadingCache<String, User> cacheByUuid;

  public UserServiceCachedImpl(UserService userService, EventBus eventBus) {
    Preconditions.checkArgument(userService != null, "userService required");
    Preconditions.checkArgument(eventBus != null, "eventBus required");

    this.eventBus = eventBus;
    this.userService = userService;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void afterPropertiesSet() {
    CacheBuilder cacheBuilder = CacheBuilder.newBuilder().maximumSize(1000).recordStats();
    cacheByEmail =
        new TransactionBoundCache<String, User>("UserCacheByEmail", cacheBuilder, loaderByEmail);
    cacheByUuid =
        new TransactionBoundCache<String, User>("UserCacheByUuid", cacheBuilder, loaderByUuid);
    eventBus.register(this);
  }

  @Subscribe
  public void onUserChanged(EntityChangedEvent<User> evt) {
    if (!evt.isTypeOf(User.class)) {
      return;
    }

    if (log.isTraceEnabled()) {
      log.trace("User changed, invalidating cache for {}", evt.getValue().getEmail());
    }

    cacheByEmail.invalidate(evt.getValue().getEmail());
    cacheByUuid.invalidate(evt.getValue().getUuid());
  }

  @Subscribe
  public void onCacheInvalidationRequest(CachesInvalidationNeeded evt) {
    cacheByEmail.invalidateAll();
    cacheByUuid.invalidateAll();
  }

  protected CacheLoader<String, User> loaderByEmail =
      new CacheLoader<>() {
        @Override
        public User load(String key) throws Exception {
          User ret = userService.getUserByEmail(key);
          if (log.isTraceEnabled()) {
            log.trace("User loaded by email {} = {}", key, ret);
          }
          return ret;
        }
      };

  protected CacheLoader<String, User> loaderByUuid =
      new CacheLoader<>() {
        @Override
        public User load(String key) throws Exception {
          User ret = userService.getUserByUuid(key);
          if (log.isTraceEnabled()) {
            log.trace("User loaded by uuid {} = {}", key, ret);
          }
          return ret;
        }
      };

  @Override
  public User createUser(User user) {
    return userService.createUser(user);
  }

  @Override
  public User getUserByUuid(String userUuid) throws UserNotFoundException {
    try {
      return cacheByUuid.get(userUuid);
    } catch (ExecutionException e) {
      Throwables.throwIfInstanceOf(e.getCause(), UserNotFoundException.class);
      Throwables.throwIfInstanceOf(e.getCause(), RuntimeException.class);
      throw new RuntimeException("Unexpected failure during requesting user by email", e);
    }
  }

  @Override
  public User getUserByEmail(String userEmail) throws ValidationException, UserNotFoundException {
    try {
      return cacheByEmail.get(userEmail);
    } catch (ExecutionException e) {
      Throwables.throwIfInstanceOf(e.getCause(), UserNotFoundException.class);
      Throwables.throwIfInstanceOf(e.getCause(), ValidationException.class);
      Throwables.throwIfInstanceOf(e.getCause(), RuntimeException.class);
      throw new RuntimeException("Unexpected failure during requesting user by email", e);
    }
  }

  @Override
  public PaginatedList<User> findUsersByDisplayNamePartial(
      String displayNamePartial, PagerParams pagerParams) {
    return userService.findUsersByDisplayNamePartial(displayNamePartial, pagerParams);
  }

  @Override
  public void updateUser(User user) throws ValidationException, UserNotFoundException {
    userService.updateUser(user);
  }

  @Override
  public void deleteUserByUuid(String userUuid) throws UserNotFoundException {
    userService.deleteUserByUuid(userUuid);
  }

  public UserService getUserService() {
    return userService;
  }

  public EventBus getEventBus() {
    return eventBus;
  }
}
