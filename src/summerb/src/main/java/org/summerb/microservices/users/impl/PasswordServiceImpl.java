package org.summerb.microservices.users.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.approaches.validation.errors.FieldRequiredValidationError;
import org.summerb.microservices.users.api.PasswordService;
import org.summerb.microservices.users.api.UserService;
import org.summerb.microservices.users.api.exceptions.UserNotFoundException;
import org.summerb.microservices.users.api.exceptions.UserServiceUnexpectedException;
import org.summerb.microservices.users.impl.dao.PasswordDao;
import org.summerb.microservices.users.impl.dom.Password;

import com.google.common.base.Preconditions;

public class PasswordServiceImpl implements PasswordService {
	private static final String FN_PASSWORD = "password";
	private UserService userService;
	private PasswordDao passwordDao;
	private PasswordEncoder passwordEncoder;

	@Override
	public boolean isUserPasswordValid(String userUuid, String passwordPlain) throws UserNotFoundException {
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
			String msg = String.format("Failed to validate user '%s' password", userUuid);
			throw new UserServiceUnexpectedException(msg, t);
		}

		return true;
	}

	protected boolean isPasswordMatch(String providedPlainPassword, String expectedHash) {
		if (expectedHash == null) {
			return false;
		}
		return passwordEncoder.matches(providedPlainPassword, expectedHash);
	}

	private void assertUserExists(String userUuid) throws UserNotFoundException {
		userService.getUserByUuid(userUuid);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void setUserPassword(String userUuid, String newPasswordPlain)
			throws UserNotFoundException, FieldValidationException {
		Preconditions.checkArgument(userUuid != null);
		Preconditions.checkArgument(newPasswordPlain != null);
		assertUserExists(userUuid);

		if (!StringUtils.hasText(newPasswordPlain)) {
			throw new FieldValidationException(new FieldRequiredValidationError(FN_PASSWORD));
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
				throw new RuntimeException("updateUserPassword returned unexpected result = " + updateResult);
			}
		} catch (Throwable t) {
			String msg = String.format("Failed to set user '%s' passwordHash '%s'", userUuid, newPasswordHash);
			throw new UserServiceUnexpectedException(msg, t);
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
				throw new RuntimeException("createRestorationToken returned unexpected result = " + updateResult);
			}

			return restorationToken;
		} catch (Throwable t) {
			String msg = String.format("Failed to create restoration token for user '%s'", userUuid);
			throw new UserServiceUnexpectedException(msg, t);
		}
	}

	@Override
	public boolean isRestorationTokenValid(String userUuid, String restorationTokenUuid) throws UserNotFoundException {
		Preconditions.checkArgument(userUuid != null);
		Preconditions.checkArgument(restorationTokenUuid != null);
		assertUserExists(userUuid);

		try {
			Password password = passwordDao.findPasswordByUserUuid(userUuid);
			if (password == null || !restorationTokenUuid.equals(password.getRestorationToken())) {
				return false;
			}
		} catch (Throwable t) {
			String msg = String.format("Failed to check user '%s' restoration token validity", userUuid);
			throw new UserServiceUnexpectedException(msg, t);
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
				throw new RuntimeException("deleteRestorationToken returned unexpected result = " + updateResult);
			}
		} catch (Throwable t) {
			String msg = String.format("Failed to delete restoration token for user '%s'", userUuid);
			throw new UserServiceUnexpectedException(msg, t);
		}
	}

	public UserService getUserService() {
		return userService;
	}

	@Required
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public PasswordDao getPasswordDao() {
		return passwordDao;
	}

	@Required
	public void setPasswordDao(PasswordDao passwordDao) {
		this.passwordDao = passwordDao;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	@Autowired
	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

}
