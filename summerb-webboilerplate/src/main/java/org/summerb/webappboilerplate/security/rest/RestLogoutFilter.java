/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
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
import java.util.Collections;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.GenericFilterBean;

public class RestLogoutFilter extends GenericFilterBean {

  protected String triggerPath = "/rest/logout";

  protected final List<LogoutHandler> handlers;

  public RestLogoutFilter() {
    this.handlers = Collections.emptyList();
  }

  public RestLogoutFilter(List<LogoutHandler> handlers) {
    if (CollectionUtils.isEmpty(handlers)) {
      this.handlers = Collections.emptyList();
    } else {
      this.handlers = Collections.unmodifiableList(handlers);
    }
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

      callHandlers(request, response, auth);
      sendResponse(response);

      return;
    }

    chain.doFilter(request, response);
  }

  protected void sendResponse(HttpServletResponse response) throws IOException {
    response.setStatus(200);
    response.flushBuffer();
  }

  protected void callHandlers(
      HttpServletRequest request, HttpServletResponse response, Authentication auth) {
    for (LogoutHandler handler : getHandlers()) {
      handler.logout(request, response, auth);
    }
  }

  protected boolean requiresLogout(HttpServletRequest request, HttpServletResponse response) {
    if (triggerPath.equalsIgnoreCase(request.getServletPath())) {
      return true;
    }
    return false;
  }

  public List<LogoutHandler> getHandlers() {
    return handlers;
  }

  public String getTriggerPath() {
    return triggerPath;
  }

  public void setTriggerPath(String basePath) {
    this.triggerPath = basePath;
  }
}
