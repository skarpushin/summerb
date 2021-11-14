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

import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.summerb.easycrud.api.dto.EntityChangedEvent;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.users.api.UserService;
import org.summerb.users.api.dto.User;
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.utils.cache.CachesInvalidationNeeded;
import org.summerb.utils.tx.TransactionBoundCache;
import org.summerb.validation.FieldValidationException;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class UserServiceCachedImpl implements UserService, InitializingBean {
	private Logger log = LogManager.getLogger(getClass());

	private UserService userService;
	private EventBus eventBus;

	private LoadingCache<String, User> cacheByEmail;
	private LoadingCache<String, User> cacheByUuid;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void afterPropertiesSet() throws Exception {
		CacheBuilder cacheBuilder = (CacheBuilder) CacheBuilder.newBuilder().maximumSize(1000).recordStats();
		cacheByEmail = new TransactionBoundCache<String, User>("UserCacheByEmail", cacheBuilder, loaderByEmail);
		cacheByUuid = new TransactionBoundCache<String, User>("UserCacheByUuid", cacheBuilder, loaderByUuid);
		eventBus.register(this);
	}

	@Subscribe
	public void onUserChanged(EntityChangedEvent<User> evt) {
		if (!evt.isTypeOf(User.class)) {
			return;
		}

		if (log.isTraceEnabled()) {
			log.trace("User changed, invalidating cache for " + evt.getValue().getEmail());
		}

		cacheByEmail.invalidate(evt.getValue().getEmail());
		cacheByUuid.invalidate(evt.getValue().getUuid());
	}

	@Subscribe
	public void onCacheInvalidationRequest(CachesInvalidationNeeded evt) {
		cacheByEmail.invalidateAll();
		cacheByUuid.invalidateAll();
	}

	private CacheLoader<String, User> loaderByEmail = new CacheLoader<String, User>() {
		@Override
		public User load(String key) throws Exception {
			User ret = userService.getUserByEmail(key);
			if (log.isTraceEnabled()) {
				log.trace("User loaded by email " + key + " = " + ret);
			}
			return ret;
		}
	};

	private CacheLoader<String, User> loaderByUuid = new CacheLoader<String, User>() {
		@Override
		public User load(String key) throws Exception {
			User ret = userService.getUserByUuid(key);
			if (log.isTraceEnabled()) {
				log.trace("User loaded by uuid " + key + " = " + ret);
			}
			return ret;
		}
	};

	@Override
	public User createUser(User user) throws FieldValidationException {
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
	public User getUserByEmail(String userEmail) throws FieldValidationException, UserNotFoundException {
		try {
			return cacheByEmail.get(userEmail);
		} catch (ExecutionException e) {
			Throwables.throwIfInstanceOf(e.getCause(), UserNotFoundException.class);
			Throwables.throwIfInstanceOf(e.getCause(), FieldValidationException.class);
			Throwables.throwIfInstanceOf(e.getCause(), RuntimeException.class);
			throw new RuntimeException("Unexpected failure during requesting user by email", e);
		}
	}

	@Override
	public PaginatedList<User> findUsersByDisplayNamePartial(String displayNamePartial, PagerParams pagerParams)
			throws FieldValidationException {
		return userService.findUsersByDisplayNamePartial(displayNamePartial, pagerParams);
	}

	@Override
	public void updateUser(User user) throws FieldValidationException, UserNotFoundException {
		userService.updateUser(user);
	}

	@Override
	public void deleteUserByUuid(String userUuid) throws UserNotFoundException {
		userService.deleteUserByUuid(userUuid);
	}

	public UserService getUserService() {
		return userService;
	}

	@Required
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	@Required
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

}
