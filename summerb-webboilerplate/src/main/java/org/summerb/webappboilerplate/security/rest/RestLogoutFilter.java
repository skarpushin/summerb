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
package org.summerb.webappboilerplate.security.rest;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.filter.GenericFilterBean;
import org.summerb.utils.json.JsonResponseWriter;
import org.summerb.utils.json.JsonResponseWriterGsonImpl;

public class RestLogoutFilter extends GenericFilterBean {
	private List<LogoutHandler> handlers;
	private String triggerPath = "/rest/logout";
	private JsonResponseWriter jsonResponseHelper;

	public RestLogoutFilter() {
		jsonResponseHelper = new JsonResponseWriterGsonImpl();
	}

	public RestLogoutFilter(JsonResponseWriter jsonResponseHelper) {
		this.jsonResponseHelper = jsonResponseHelper;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (requiresLogout(request, response)) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			if (logger.isDebugEnabled()) {
				logger.debug("Logging out user '" + auth + "' and transferring to logout destination");
			}

			for (LogoutHandler handler : handlers) {
				handler.logout(request, response, auth);
			}

			response.setStatus(200);
			jsonResponseHelper.writeResponseBody("Logged out", response);
			return;
		}

		chain.doFilter(request, response);
	}

	private boolean requiresLogout(HttpServletRequest request, HttpServletResponse response) {
		if (triggerPath.equalsIgnoreCase(request.getServletPath())) {
			return true;
		}
		return false;
	}

	public List<LogoutHandler> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<LogoutHandler> handlers) {
		this.handlers = handlers;
	}

	public String getTriggerPath() {
		return triggerPath;
	}

	public void setTriggerPath(String basePath) {
		this.triggerPath = basePath;
	}

}
