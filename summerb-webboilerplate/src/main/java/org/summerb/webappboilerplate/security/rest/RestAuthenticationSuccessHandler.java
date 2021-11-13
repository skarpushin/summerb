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
package org.summerb.webappboilerplate.security.rest;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.summerb.utils.json.JsonResponseWriter;
import org.summerb.utils.json.JsonResponseWriterGsonImpl;
import org.summerb.webappboilerplate.security.dto.LoginResult;
import org.summerb.webappboilerplate.security.impls.UserDetailsImpl;

public class RestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private JsonResponseWriter jsonResponseHelper;

	/**
	 * Should be the same one as used by
	 * {@link org.springframework.security.web.access.ExceptionTranslationFilter}.
	 * 
	 * And
	 * {@link org.springframework.security.web.savedrequest.RequestCacheAwareFilter}
	 * if used
	 */
	private RequestCache requestCache = new NullRequestCache();

	public RestAuthenticationSuccessHandler() {
		jsonResponseHelper = new JsonResponseWriterGsonImpl();
	}

	public RestAuthenticationSuccessHandler(JsonResponseWriter jsonResponseHelper) {
		this.jsonResponseHelper = jsonResponseHelper;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		HttpSession session = request.getSession(true);
		LoginResult ret = new LoginResult();
		ret.setUser(((UserDetailsImpl) authentication.getPrincipal()).getUser());
		addCustomAttrsIfAny(session, request, response, ret);
		jsonResponseHelper.writeResponseBody(ret, response);
	}

	protected void addCustomAttrsIfAny(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			LoginResult ret) {

		SavedRequest savedRequest = requestCache.getRequest(request, response);
		if (savedRequest != null) {
			ret.setAttributes(new HashMap<>());
			ret.getAttributes().put(LoginResult.ATTR_REDIRECT_TO, savedRequest.getRedirectUrl());

			requestCache.removeRequest(request, response);
		}
	}

	public RequestCache getRequestCache() {
		return requestCache;
	}

	@Autowired(required = false)
	public void setRequestCache(RequestCache requestCache) {
		this.requestCache = requestCache;
	}
}
