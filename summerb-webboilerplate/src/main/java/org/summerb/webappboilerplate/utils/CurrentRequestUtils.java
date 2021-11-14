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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Utility methods providing information related to current request
 * 
 * @author sergey.karpushin
 * 
 */
public class CurrentRequestUtils {
	private static Logger log = LogManager.getLogger(CurrentRequestUtils.class);
	private static Locale defaultLocale = new Locale("en", "US");

	/**
	 * Get current request
	 * 
	 * @return
	 */
	public static HttpServletRequest get() {
		ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		return ra.getRequest();
	}

	/**
	 * Get locale for curent request
	 * 
	 * @return
	 */
	public static Locale getLocale() {
		// Plan A: Try to get it from locale context holder
		Locale locale = LocaleContextHolder.getLocale();
		if (locale != null) {
			return locale;
		}

		// Plan B: Try to get it from request
		HttpServletRequest request = get();
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		if (localeResolver == null) {
			log.warn("Cannot get locale resolver. Will fallback to: " + defaultLocale);
			return defaultLocale;
		}
		locale = localeResolver.resolveLocale(request);
		if (locale != null) {
			return locale;
		}

		// fallback
		log.warn("Cannot resolve locale. Will fallback to: " + defaultLocale);
		return defaultLocale;
	}

	/**
	 * @deprecated WARNING this method won't work correctly in async environment
	 *             (like i.e. when using DeferredResult)
	 */
	@Deprecated
	public static WebApplicationContext getWac() {
		return RequestContextUtils.findWebApplicationContext(get());
	}

	public static String getBaseUrl() {
		HttpServletRequest req = get();
		String ret = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
		return ret;
	}
}
