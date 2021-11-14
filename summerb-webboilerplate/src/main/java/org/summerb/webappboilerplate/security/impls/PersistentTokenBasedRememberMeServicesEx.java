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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.summerb.webappboilerplate.security.apis.RememberMeRequestedStrategy;

/**
 * Had to extend standard PersistentTokenBasedRememberMeServices because
 * initially needed to override
 * {@link #rememberMeRequested(HttpServletRequest, String)}, but it's seems
 * weird to extend whole class to override just one method. So decided to put a
 * strategy here
 * 
 * @author sergeyk
 * 
 */
public class PersistentTokenBasedRememberMeServicesEx extends PersistentTokenBasedRememberMeServices {
	protected Logger log = LogManager.getLogger(getClass());

	private RememberMeRequestedStrategy rememberMeRequestedStrategy = new RememberMeRequestedStrategyImpl();

	public PersistentTokenBasedRememberMeServicesEx(String key, UserDetailsService userDetailsService,
			PersistentTokenRepository tokenRepository) {
		super(key, userDetailsService, tokenRepository);
	}

	@Override
	protected UserDetails processAutoLoginCookie(String[] arg0, HttpServletRequest arg1, HttpServletResponse arg2) {
		try {
			return super.processAutoLoginCookie(arg0, arg1, arg2);
		} catch (CookieTheftException cte) {
			log.warn("Instead of throwing CookieTheftException, will convert it to RememberMeAuthenticationException",
					cte);
			// NOTE: It will not prevent all user cookies delition, but still
			// will not show ugly exception to the user, instead it will ask for
			// login
			throw new RememberMeAuthenticationException("Converting CookieTheftException to something less scary");
		}
	}

	@Override
	protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
		return rememberMeRequestedStrategy.isRememberMeRequested(request);
	}

	public RememberMeRequestedStrategy getRememberMeRequestedStrategy() {
		return rememberMeRequestedStrategy;
	}

	public void setRememberMeRequestedStrategy(RememberMeRequestedStrategy rememberMeRequestedStrategy) {
		this.rememberMeRequestedStrategy = rememberMeRequestedStrategy;
	}
}
