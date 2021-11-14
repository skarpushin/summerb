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
package org.summerb.users.i18n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.summerb.spring.security.SecurityConstants;
import org.summerb.spring.security.api.SecurityContextResolver;
import org.summerb.users.api.UserService;
import org.summerb.users.api.dto.User;
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.validation.FieldValidationException;

public class LocaleResolverUserBasedImpl extends CookieLocaleResolver {
	private Logger log = LogManager.getLogger(getClass());

	@Autowired
	private SecurityContextResolver<User> securityContextResolver;
	@Autowired
	private UserService userService;

	public Locale resolveLocaleFromCookie(HttpServletRequest request) {
		return super.resolveLocale(request);
	}

	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		try {
			boolean isRegisteredUser = securityContextResolver.hasRole(SecurityConstants.ROLE_USER);
			if (isRegisteredUser) {
				User user = securityContextResolver.getUser();
				Locale userLocale = StringUtils.parseLocaleString(user.getLocale());
				return userLocale;
			}
		} catch (Throwable t) {
			log.warn("Failed to resolve locale based on user data", t);
		}

		return super.resolveLocale(request);
	}

	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		try {
			boolean isRegisteredUser = securityContextResolver.hasRole(SecurityConstants.ROLE_USER);
			if (isRegisteredUser) {
				updateUserProfileWithNewLocale(locale.toString());
			}
		} catch (Throwable t) {
			log.warn("Failed to resolve locale based on user data", t);
		}

		super.setLocale(request, response, locale);
	}

	private void updateUserProfileWithNewLocale(String newLocale)
			throws UserNotFoundException, FieldValidationException {
		User user = securityContextResolver.getUser();
		if (user.getLocale().equalsIgnoreCase(newLocale)) {
			return;
		}

		user.setLocale(newLocale);
		userService.updateUser(user);
	}

}
