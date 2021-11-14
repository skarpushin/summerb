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

import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.summerb.i18n.CommonMessageCodes;
import org.summerb.security.elevation.impl.ElevationRunnerImpl;
import org.summerb.spring.security.SecurityConstants;
import org.summerb.spring.security.SecurityMessageCodes;
import org.summerb.users.api.AuthTokenService;
import org.summerb.users.api.PasswordService;
import org.summerb.users.api.PermissionService;
import org.summerb.users.api.UserService;
import org.summerb.users.api.dto.User;
import org.summerb.users.api.exceptions.InvalidPasswordException;
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.users.api.exceptions.UserServiceUnexpectedException;
import org.summerb.utils.exceptions.GenericException;
import org.summerb.validation.FieldValidationException;
import org.summerb.validation.ValidationContext;
import org.summerb.webappboilerplate.security.apis.LoginEligibilityVerifier;
import org.summerb.webappboilerplate.security.apis.PasswordResetArmedHandler;
import org.summerb.webappboilerplate.security.apis.RegistrationActivatedHandler;
import org.summerb.webappboilerplate.security.apis.UserRegisteredHandler;
import org.summerb.webappboilerplate.security.apis.UsersServiceFacade;
import org.summerb.webappboilerplate.security.dto.LoginParams;
import org.summerb.webappboilerplate.security.dto.PasswordChange;
import org.summerb.webappboilerplate.security.dto.PasswordReset;
import org.summerb.webappboilerplate.security.dto.Registration;
import org.summerb.webappboilerplate.security.dto.UserStatus;
import org.summerb.webappboilerplate.security.elevation.ElevationStrategyRunAsUserImpl;
import org.summerb.webappboilerplate.security.ve.PasswordsDontMatchValidationError;
import org.summerb.webappboilerplate.security.ve.RegistrationActivationRequiredValidationError;
import org.summerb.webappboilerplate.security.ve.RegistrationAlreadyRequestedValidationError;
import org.summerb.webappboilerplate.security.ve.RegistrationRequiredValidationError;
import org.summerb.webappboilerplate.utils.CurrentRequestUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

/**
 * Default impl for {@link UsersServiceFacade}, assuming classic operations with
 * user accounts.
 * 
 * It utilizes summerb's UserService.
 * 
 * Set of handlers can be used to perform further steps after successful actions
 * like {@link #userRegisteredHandler}, {@link #passwordResetArmedHandler},
 * {@link #registrationActivatedHandler}
 * 
 * @author sergeyk
 *
 */
public class UsersServiceFacadeImpl implements UsersServiceFacade, LoginEligibilityVerifier {
	private Logger log = LogManager.getLogger(getClass());

	private int passwordMinLength = 4;
	private AuthTokenService authTokenService;
	private PermissionService permissionService;
	private UserService userService;
	private PasswordService passwordService;
	private PasswordEncoder passwordEncoder;

	private UserRegisteredHandler userRegisteredHandler;
	private PasswordResetArmedHandler passwordResetArmedHandler;
	private RegistrationActivatedHandler registrationActivatedHandler;

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public User registerUser(Registration registration) throws FieldValidationException {
		try {
			Preconditions.checkArgument(registration != null, "Registration param must be not null");

			// Validate display name
			validateRegistration(registration);

			// Validate user status
			UserStatus userStatus = getUserStatusByEmail(registration.getEmail());
			if (userStatus == UserStatus.AwaitingActivation) {
				throw new FieldValidationException(new RegistrationAlreadyRequestedValidationError());
			}

			// Create user
			User user = null;
			if (userStatus == UserStatus.Provisioned) {
				user = userService.getUserByEmail(registration.getEmail());
				user.setDisplayName(registration.getDisplayName());
				user.setLocale(CurrentRequestUtils.getLocale().toString());
				user.setTimeZone(TimeZone.getDefault().getID());
				userService.updateUser(user);
			} else {
				user = new User();
				user.setEmail(registration.getEmail());
				user.setDisplayName(registration.getDisplayName());
				user.setLocale(CurrentRequestUtils.getLocale().toString());
				user.setTimeZone(TimeZone.getDefault().getID());
				user = userService.createUser(user);
			}

			// Create password
			passwordService.setUserPassword(user.getUuid(), registration.getPassword());

			// Create user account permissions
			permissionService.grantPermission(SecurityConstants.DOMAIN, user.getUuid(), null,
					SecurityConstants.MARKER_AWAITING_ACTIVATION);

			runUserRegisteredHandler(user);

			//
			return user;
		} catch (UserNotFoundException e) {
			throw new UserServiceUnexpectedException("User was just created, but not found", e);
		} catch (Throwable t) {
			Throwables.throwIfInstanceOf(t, FieldValidationException.class);
			throw new RuntimeException("Unexpected error while registering user", t);
		}
	}

	protected void runUserRegisteredHandler(final User user) {
		if (userRegisteredHandler == null) {
			return;
		}

		List<String> perms = permissionService.findUserPermissionsForSubject(SecurityConstants.DOMAIN, user.getUuid(),
				null);
		ElevationRunnerImpl runAs = new ElevationRunnerImpl(new ElevationStrategyRunAsUserImpl<User>(user, perms));
		runAs.runElevated(new Runnable() {
			@Override
			public void run() {
				userRegisteredHandler.onUserRegistered(user);
			}
		});
	}

	protected void validateRegistration(Registration registration) throws FieldValidationException {
		ValidationContext ctx = new ValidationContext();
		validateDisplayName(registration.getDisplayName(), ctx);
		validatePassword(registration.getPassword(), ctx);
		ctx.validateEmailFormat(registration.getEmail(), User.FN_EMAIL);

		ctx.throwIfHasErrors();
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public String getNewPasswordResetToken(String email) throws FieldValidationException {
		try {
			validateUserIsEligableForPasswordReset(email);
			User user = userService.getUserByEmail(email);
			String passwordResetToken = passwordService.getNewRestorationTokenForUser(user.getUuid());

			if (passwordResetArmedHandler != null) {
				passwordResetArmedHandler.onPasswordResetRequested(user, passwordResetToken);
			}

			return passwordResetToken;
		} catch (Throwable e) {
			Throwables.throwIfInstanceOf(e, FieldValidationException.class);
			throw new UserServiceUnexpectedException("Failed to arrange password reset", e);
		}
	}

	protected void validateUserIsEligableForPasswordReset(String email) throws FieldValidationException {
		ValidationContext ctx = new ValidationContext();
		if (!ctx.validateEmailFormat(email, User.FN_EMAIL)) {
			throw new FieldValidationException(ctx.getErrors());
		}

		// now see if this user exists
		UserStatus userStatus = getUserStatusByEmail(email);
		if (userStatus == UserStatus.NotExists || userStatus == UserStatus.Provisioned) {
			throw new FieldValidationException(new RegistrationRequiredValidationError());
		}

		// Sanity check
		if (userStatus != UserStatus.NormalUser && userStatus != UserStatus.AwaitingActivation) {
			throw new RuntimeException(
					"Password Reset scenario is not supported for user whose status is: " + userStatus);
		}
	}

	protected void validatePassword(String password, ValidationContext ctx) {
		if (ctx.validateNotEmpty(password, LoginParams.FN_PASSWORD)) {
			ctx.validateLengthGreaterOrEqual(password, passwordMinLength, LoginParams.FN_PASSWORD);
		}
	}

	protected void validateDisplayName(String name, ValidationContext ctx) {
		ctx.validateNotEmpty(name, Registration.FN_DISPLAY_NAME);
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public void activateRegistration(String userUuid) throws GenericException {
		try {
			// Validate
			if (!StringUtils.hasText(userUuid)) {
				throw new GenericException(SecurityMessageCodes.NEED_ACTIVATION_TOKEN);
			}

			// Search user
			User user = userService.getUserByUuid(userUuid);

			boolean awaitingActivation = isAccountRequiresActivation(userUuid);
			if (awaitingActivation) {
				activateAccount(user.getUuid());
			} else {
				throw new GenericException(SecurityMessageCodes.ALREADY_ACTIVATED);
			}

			if (registrationActivatedHandler != null) {
				registrationActivatedHandler.onRegistrationActivated(user);
			}

		} catch (Throwable e) {
			log.error("Failed to activate registration", e);
			throw new GenericException(SecurityMessageCodes.FAILED_TO_ACTIVATE_REGISTRATION, e);
		}
	}

	protected boolean isAccountRequiresActivation(String userUuid) {
		return permissionService.hasPermission(SecurityConstants.DOMAIN, userUuid, null,
				SecurityConstants.MARKER_AWAITING_ACTIVATION);
	}

	protected void activateAccount(String userUuid) {
		permissionService.revokePermission(SecurityConstants.DOMAIN, userUuid, null,
				SecurityConstants.MARKER_AWAITING_ACTIVATION);
		permissionService.grantPermission(SecurityConstants.DOMAIN, userUuid, null, SecurityConstants.ROLE_USER);
	}

	@Override
	public void validateUserAllowedToLogin(String username) throws FieldValidationException {
		// Check if use have to activate his account
		UserStatus userStatus = getUserStatusByEmail(username);
		if (userStatus == UserStatus.AwaitingActivation) {
			throw new FieldValidationException(new RegistrationActivationRequiredValidationError());
		}
		if (userStatus == UserStatus.Provisioned) {
			throw new FieldValidationException(new RegistrationRequiredValidationError());
		}
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public void resetPassword(String email, String passwordResetToken, PasswordReset resetPasswordRequest)
			throws UserNotFoundException, FieldValidationException {

		try {
			String userUuid = assertPasswordResetOperationValid(email, passwordResetToken, resetPasswordRequest);

			passwordService.setUserPassword(userUuid, resetPasswordRequest.getPassword());

			// generate new token in order to invalidate current
			passwordService.getNewRestorationTokenForUser(userUuid);

			// If account requires activation, do it
			if (isAccountRequiresActivation(userUuid)) {
				activateAccount(userUuid);
			}
		} catch (Throwable e) {
			Throwables.throwIfInstanceOf(e, FieldValidationException.class);
			throw new UserServiceUnexpectedException("Failed to arrange password reset", e);
		}
	}

	@Override
	public void changePassword(String email, PasswordChange passwordChange)
			throws UserNotFoundException, FieldValidationException {

		try {
			User user = validatePasswordChangeRequestValid(email, passwordChange);
			passwordService.setUserPassword(user.getUuid(), passwordChange.getPassword());
		} catch (Throwable e) {
			Throwables.throwIfInstanceOf(e, FieldValidationException.class);
			throw new UserServiceUnexpectedException("Failed to arrange password reset", e);
		}
	}

	protected User validatePasswordChangeRequestValid(String email, PasswordChange passwordChange)
			throws UserNotFoundException, FieldValidationException {
		ValidationContext ctx = new ValidationContext();

		ctx.lengthEqOrGreater(passwordChange.getPassword(), 4, LoginParams.FN_PASSWORD);
		ctx.equals(passwordChange.getPassword(), SecurityMessageCodes.NEW_PASSWORD,
				passwordChange.getNewPasswordAgain(), SecurityMessageCodes.NEW_PASSWORD_AGAIN,
				PasswordChange.FN_NEW_PASSWORD_AGAIN);

		User user = null;
		if (ctx.hasText(passwordChange.getCurrentPassword(), PasswordChange.FN_CURRENT_PASSWORD)) {
			user = userService.getUserByEmail(email);
			ctx.isTrue(passwordService.isUserPasswordValid(user.getUuid(), passwordChange.getCurrentPassword()),
					InvalidPasswordException.ERROR_LOGIN_INVALID_PASSWORD, PasswordChange.FN_CURRENT_PASSWORD);
		}

		ctx.throwIfHasErrors();
		return user;
	}

	protected String assertPasswordResetOperationValid(String email, String passwordResetToken,
			PasswordReset resetPasswordRequest)
			throws FieldValidationException, UserNotFoundException, GenericException {
		validatePasswordReset(resetPasswordRequest);
		try {
			validateUserIsEligableForPasswordReset(email);
		} catch (FieldValidationException fve) {
			throw new GenericException(CommonMessageCodes.ERROR_UNEXPECTED, fve);
		}

		User user = userService.getUserByEmail(email);
		String userUuid = user.getUuid();
		boolean isValid = passwordService.isRestorationTokenValid(userUuid, passwordResetToken);
		if (!isValid) {
			throw new GenericException(SecurityMessageCodes.INVALID_PASSWORD_RESET_TOKEN);
		}
		return userUuid;
	}

	protected void validatePasswordReset(PasswordReset resetPasswordRequest) throws FieldValidationException {
		ValidationContext ctx = new ValidationContext();
		validatePassword(resetPasswordRequest.getPassword(), ctx);
		if (!resetPasswordRequest.getPassword().equals(resetPasswordRequest.getNewPasswordAgain())) {
			ctx.add(new PasswordsDontMatchValidationError());
		}
		ctx.throwIfHasErrors();
	}

	@Override
	public UserStatus getUserStatusByEmail(String email) throws FieldValidationException {
		ValidationContext ctx = new ValidationContext();
		ctx.validateNotEmpty(email, LoginParams.FN_EMAIL);
		ctx.throwIfHasErrors();

		// Check if user have record
		User user = null;
		try {
			user = userService.getUserByEmail(email);
		} catch (UserNotFoundException nfe) {
			return UserStatus.NotExists;
		}

		// Check if user has ROLE_USER
		List<String> permissions = permissionService.findUserPermissionsForSubject(SecurityConstants.DOMAIN,
				user.getUuid(), null);

		if (permissions.contains(SecurityConstants.ROLE_USER)) {
			return UserStatus.NormalUser;
		}

		if (permissions.contains(SecurityConstants.MARKER_AWAITING_ACTIVATION)) {
			return UserStatus.AwaitingActivation;
		}

		return UserStatus.Provisioned;
	}

	public AuthTokenService getAuthTokenService() {
		return authTokenService;
	}

	@Autowired
	public void setAuthTokenService(AuthTokenService authTokenService) {
		this.authTokenService = authTokenService;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	@Autowired
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public UserService getUserService() {
		return userService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public PasswordService getPasswordService() {
		return passwordService;
	}

	@Autowired
	public void setPasswordService(PasswordService passwordService) {
		this.passwordService = passwordService;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	@Autowired
	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User getUserByEmail(String email) throws UserNotFoundException, FieldValidationException {
		return userService.getUserByEmail(email);
	}

	@Override
	public boolean isPasswordResetTokenValid(String userEmail, String passwordResetToken)
			throws UserNotFoundException, FieldValidationException {
		User user = getUserByEmail(userEmail);
		return passwordService.isRestorationTokenValid(user.getUuid(), passwordResetToken);
	}

	public int getPasswordMinLength() {
		return passwordMinLength;
	}

	public void setPasswordMinLength(int passwordMinLength) {
		this.passwordMinLength = passwordMinLength;
	}

	public UserRegisteredHandler getUserRegisteredHandler() {
		return userRegisteredHandler;
	}

	public void setUserRegisteredHandler(UserRegisteredHandler userRegisteredHandler) {
		this.userRegisteredHandler = userRegisteredHandler;
	}

	public PasswordResetArmedHandler getPasswordResetArmedHandler() {
		return passwordResetArmedHandler;
	}

	public void setPasswordResetArmedHandler(PasswordResetArmedHandler passwordResetArmedHandler) {
		this.passwordResetArmedHandler = passwordResetArmedHandler;
	}

	public RegistrationActivatedHandler getRegistrationActivatedHandler() {
		return registrationActivatedHandler;
	}

	public void setRegistrationActivatedHandler(RegistrationActivatedHandler registrationActivatedHandler) {
		this.registrationActivatedHandler = registrationActivatedHandler;
	}

}
