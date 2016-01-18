package org.summerb.microservices.users.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.summerb.approaches.jdbccrud.api.dto.EntityChangedEvent;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.approaches.validation.ValidationContext;
import org.summerb.approaches.validation.ValidationUtils;
import org.summerb.microservices.users.api.UserService;
import org.summerb.microservices.users.api.dto.User;
import org.summerb.microservices.users.api.exceptions.UserNotFoundException;
import org.summerb.microservices.users.api.exceptions.UserServiceUnexpectedException;
import org.summerb.microservices.users.api.validation.DuplicateUserValidationError;
import org.summerb.microservices.users.impl.dao.UserDao;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

public class UserServiceImpl implements UserService {
	private UserDao userDao;
	private EventBus eventBus;

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public User createUser(User userTemplate) throws FieldValidationException {
		Assert.notNull(userTemplate);

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
			ValidationUtils.isValidNotNullableUuid(userTemplate.getUuid());
			userToCreate.setUuid(userTemplate.getUuid());
		} else {
			userToCreate.setUuid(UUID.randomUUID().toString());
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
			throw new FieldValidationException(new DuplicateUserValidationError(User.FN_EMAIL));
		} catch (Throwable t) {
			String msg = String.format("Failed to create user with email '%s'", userToCreate.getEmail());
			throw new UserServiceUnexpectedException(msg, t);
		}

		return userToCreate;
	}

	private void validateUser(User user) throws FieldValidationException {
		ValidationContext ctx = new ValidationContext();

		validateEmail(user.getEmail(), ctx);
		ctx.validateDataLengthLessOrEqual(user.getDisplayName(), User.FN_DISPLAY_NAME_SIZE, User.FN_DISPLAY_NAME);

		if (ctx.getHasErrors()) {
			throw new FieldValidationException(ctx.getErrors());
		}
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
	public User getUserByEmail(String userEmail) throws FieldValidationException, UserNotFoundException {
		Assert.notNull(userEmail, "user email must be provided");
		validateEmail(userEmail);

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

	private void validateEmail(String email) throws FieldValidationException {
		ValidationContext ctx = new ValidationContext();

		validateEmail(email, ctx);

		if (ctx.getHasErrors()) {
			throw new FieldValidationException(ctx.getErrors());
		}
	}

	protected void validateEmail(String email, ValidationContext ctx) {
		if (ctx.validateNotEmpty(email, User.FN_EMAIL)) {
			ctx.validateEmailFormat(email, User.FN_EMAIL);
			ctx.validateDataLengthLessOrEqual(email, User.FN_EMAIL_SIZE, User.FN_EMAIL);
		}
	}

	@Override
	public PaginatedList<User> findUsersByDisplayNamePartial(String displayNamePartial, PagerParams pagerParams)
			throws FieldValidationException {
		Preconditions.checkArgument(StringUtils.hasText(displayNamePartial), "Query text must be specified");

		try {
			return userDao.findUserByDisplayNamePartial(displayNamePartial, pagerParams);
		} catch (Throwable t) {
			String msg = String.format("Failed to search users by partial user name '%s'", displayNamePartial);
			throw new UserServiceUnexpectedException(msg, t);
		}
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void updateUser(User user) throws FieldValidationException, UserNotFoundException {
		Preconditions.checkArgument(user != null, "User reference required");
		Preconditions.checkArgument(StringUtils.hasText(user.getUuid()), "User uuid must be provided");

		validateUser(user);

		boolean isUpdatedSuccessfully;
		try {
			isUpdatedSuccessfully = userDao.updateUser(user);
			eventBus.post(EntityChangedEvent.updated(user));
		} catch (DuplicateKeyException dke) {
			throw new FieldValidationException(new DuplicateUserValidationError(User.FN_EMAIL));
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

}
