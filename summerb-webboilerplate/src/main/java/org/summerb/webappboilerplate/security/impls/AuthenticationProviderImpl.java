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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.summerb.spring.security.SecurityConstants;
import org.summerb.spring.security.SecurityMessageCodes;
import org.summerb.users.api.PasswordService;
import org.summerb.users.api.PermissionService;
import org.summerb.users.api.UserService;
import org.summerb.users.api.dto.User;
import org.summerb.users.api.exceptions.InvalidPasswordException;
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.validation.FieldValidationException;
import org.summerb.webappboilerplate.security.apis.LoginEligibilityVerifier;
import org.summerb.webappboilerplate.security.ve.PasswordInvalidValidationError;
import org.summerb.webappboilerplate.security.ve.UserNotFoundValidationError;
import org.summerb.webappboilerplate.utils.CurrentRequestUtils;

/**
 * Our own impl that uses {@link UserService} and {@link PermissionService}
 * 
 * @author sergeyk
 *
 */
public class AuthenticationProviderImpl implements AuthenticationProvider, InitializingBean, ApplicationContextAware {
	protected final Logger logger = LogManager.getLogger(getClass());

	private PasswordEncoder passwordEncoder;
	private PasswordService passwordService;
	private ApplicationContext applicationContext;
	private PermissionService permissionService;
	private UserService userService;
	private LoginEligibilityVerifier loginEligibilityVerifier;

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// Ensure that all conditions apply
		Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication,
				getMessage("AbstractUserDetailsAuthenticationProvider.onlySupports",
						"Only UsernamePasswordAuthenticationToken is supported"));
		// check we have credentials specified
		if (authentication.getCredentials() == null) {
			logger.debug("Authentication failed: no credentials provided");
			throw new BadCredentialsException(
					getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}

		// Determine user-name
		String username = (authentication.getPrincipal() == null) ? "" : authentication.getName();
		// Encode password
		String presentedPlainPassword = authentication.getCredentials().toString();

		try {
			if (loginEligibilityVerifier != null) {
				loginEligibilityVerifier.validateUserAllowedToLogin(username);
			}

			// Proceed with authentication
			// get user
			User user = userService.getUserByEmail(username);
			// check password
			if (!passwordService.isUserPasswordValid(user.getUuid(), presentedPlainPassword)) {
				throw new InvalidPasswordException();
			}
			// get user permission
			List<String> permissions = permissionService.findUserPermissionsForSubject(SecurityConstants.DOMAIN,
					user.getUuid(), null);

			UserDetailsImpl userDetails = new UserDetailsImpl(user, "[PASSWORD REMOVED]", permissions, null);

			UsernamePasswordAuthenticationToken ret = new UsernamePasswordAuthenticationToken(userDetails,
					authentication.getCredentials(), userDetails.getAuthorities());
			ret.setDetails(authentication.getDetails());
			return ret;
		} catch (FieldValidationException e) {
			throw buildBadCredentialsExc(e);
		} catch (UserNotFoundException e) {
			throw buildBadCredentialsExc(new FieldValidationException(new UserNotFoundValidationError()));
		} catch (InvalidPasswordException e) {
			throw buildBadCredentialsExc(new FieldValidationException(new PasswordInvalidValidationError()));
		} catch (Throwable t) {
			throw new AuthenticationServiceException(
					getMessage(SecurityMessageCodes.AUTH_FATAL, "Fatal authentication exception"), t);
		}
	}

	protected BadCredentialsException buildBadCredentialsExc(FieldValidationException e) {
		return new BadCredentialsException(getMessage(SecurityMessageCodes.BAD_CREDENTIALS, "Bad credentials"), e);
	}

	private String getMessage(String msgCode, String defaultValue) {
		return applicationContext.getMessage(msgCode, null, defaultValue, CurrentRequestUtils.getLocale());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

	@Autowired
	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
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

	public LoginEligibilityVerifier getLoginEligibilityVerifier() {
		return loginEligibilityVerifier;
	}

	@Autowired(required = false)
	public void setLoginEligibilityVerifier(LoginEligibilityVerifier loginEligibilityVerifier) {
		this.loginEligibilityVerifier = loginEligibilityVerifier;
	}
}
