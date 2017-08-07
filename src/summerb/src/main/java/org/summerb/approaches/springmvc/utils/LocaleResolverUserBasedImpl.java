package org.summerb.approaches.springmvc.utils;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.summerb.approaches.security.api.SecurityContextResolver;
import org.summerb.approaches.springmvc.security.SecurityConstants;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.microservices.users.api.UserService;
import org.summerb.microservices.users.api.dto.User;
import org.summerb.microservices.users.api.exceptions.UserNotFoundException;

public class LocaleResolverUserBasedImpl extends CookieLocaleResolver {
	private Logger log = Logger.getLogger(getClass());

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
