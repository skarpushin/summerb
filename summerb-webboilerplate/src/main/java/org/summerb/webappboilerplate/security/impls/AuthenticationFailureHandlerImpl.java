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
package org.summerb.webappboilerplate.security.impls;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

/**
 * This impl was created to pass userName to the login form so that user don't
 * have to enter it after failed previos login attempt
 * 
 * @author sergey.k
 * 
 */
public class AuthenticationFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {
	private static final String ATTR_LAST_FAILED_USER_NAME = "userName";
	private String defaultFailureUrl;

	@Autowired
	private UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter;

	public AuthenticationFailureHandlerImpl() {

	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String usernameParameter = usernamePasswordAuthenticationFilter.getUsernameParameter();
		String userName = request.getParameter(usernameParameter);
		saveException(request, exception);
		if (StringUtils.hasText(userName)) {
			getRedirectStrategy().sendRedirect(request, response,
					defaultFailureUrl + "?" + ATTR_LAST_FAILED_USER_NAME + "=" + userName);
		} else {
			getRedirectStrategy().sendRedirect(request, response, defaultFailureUrl);
		}
	}

	@Override
	public void setDefaultFailureUrl(String defaultFailureUrl) {
		this.defaultFailureUrl = defaultFailureUrl;
		super.setDefaultFailureUrl(defaultFailureUrl);
	}

}
