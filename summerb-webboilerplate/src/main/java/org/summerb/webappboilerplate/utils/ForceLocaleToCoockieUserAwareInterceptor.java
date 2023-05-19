/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.summerb.webappboilerplate.users.i18n.LocaleResolverUserBasedImpl;

/**
 * Will force to save locale in cookie. We need this in order to fix bug with IE. Because it will
 * incorrectly process locale for ajax requests. Instead of "ru_RU" as for regular GET it will send
 * "ru" for XHR. This will lead to incorrect locale and currency processing.
 *
 * <p>This impl can read user settings and default locale to the one chosen by user
 *
 * @author sergey.k
 */
public class ForceLocaleToCoockieUserAwareInterceptor implements AsyncHandlerInterceptor {
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);

    // Check if it's our case
    if (localeResolver == null) {
      throw new IllegalStateException(
          "No LocaleResolver found: not in a DispatcherServlet request?");
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
