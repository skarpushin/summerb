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
package org.summerb.webappboilerplate.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

/**
 * Will force to save locale in cookie. We need this in order to fix bug with
 * IE. Because it will incorrectly process locale for ajax requests. Instead of
 * "ru_RU" as for regular GET it will send "ru" for XHR. This will lead to
 * incorrect locale and currency processing.
 * 
 * @author sergey.karpushin
 * 
 */
public class ForceLocaleToCoockieInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);

		// Check if it's our case
		if (localeResolver == null) {
			throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
		}
		if (!(localeResolver instanceof CookieLocaleResolver)) {
			return true;
		}

		// Check if locale not in cookie.
		// If so, then force it to store in cookie
		CookieLocaleResolver cookieLocaleResolver = (CookieLocaleResolver) localeResolver;
		Cookie cookie = WebUtils.getCookie(request, cookieLocaleResolver.getCookieName());
		if (cookie == null) {
			cookieLocaleResolver.setLocale(request, response, localeResolver.resolveLocale(request));
		}

		return true;
	}
}
