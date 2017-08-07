package org.summerb.approaches.springmvc.utils;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Will force to save locale in cookie. We need this in order to fix bug with
 * IE. Because it will incorrectly process locale for ajax requests. Instead of
 * "ru_RU" as for regular GET it will send "ru" for XHR. This will lead to
 * incorrect locale and currency processing.
 * 
 * This impl can read user settings and default locale to the one chosen by user
 * 
 * @author sergey.k
 * 
 */
public class ForceLocaleToCoockieUserAwareInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);

		// Check if it's our case
		if (localeResolver == null) {
			throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
		}
		if (!(localeResolver instanceof LocaleResolverUserBasedImpl)) {
			return true;
		}

		// Check if locale not in cookie.
		// If so, then force it to store in cookie
		LocaleResolverUserBasedImpl cookieLocaleResolver = (LocaleResolverUserBasedImpl) localeResolver;
		Locale localeFromCookie = cookieLocaleResolver.resolveLocaleFromCookie(request);
		Locale localeFromUser = cookieLocaleResolver.resolveLocale(request);
		if (localeFromUser != null && !localeFromUser.equals(localeFromCookie)) {
			cookieLocaleResolver.setLocale(request, response, localeFromUser);
		}

		return true;
	}
}