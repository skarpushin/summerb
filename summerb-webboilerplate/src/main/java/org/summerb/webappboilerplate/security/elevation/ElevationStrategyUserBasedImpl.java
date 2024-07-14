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
package org.summerb.webappboilerplate.security.elevation;

import com.google.common.base.Preconditions;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.util.StringUtils;
import org.summerb.security.elevation.api.ElevationStrategy;
import org.summerb.spring.security.SecurityConstants;
import org.summerb.users.api.PermissionService;
import org.summerb.users.api.UserService;
import org.summerb.users.api.dto.User;
import org.summerb.webappboilerplate.security.impls.UserDetailsImpl;

/**
 * This impl elevates to BackgroundProcess credentials if current user is missing or ANonymous
 *
 * @author sergey.karpushin
 */
public class ElevationStrategyUserBasedImpl implements ElevationStrategy, InitializingBean {
  @Autowired protected UserService userService;
  @Autowired protected PermissionService permissionService;

  protected String userEmail;
  protected Authentication authentication;
  protected boolean force;

  public ElevationStrategyUserBasedImpl(String userEmail) {
    this(userEmail, false);
  }

  public ElevationStrategyUserBasedImpl(String userEmail, boolean force) {
    Preconditions.checkArgument(StringUtils.hasText(userEmail));
    this.userEmail = userEmail;
    this.force = force;
  }

  @Override
  public void afterPropertiesSet() {
    getAuthentication(); // eagerly load user
  }

  @Override
  public boolean isElevationRequired() {
    return force
        || (SecurityContextHolder.getContext() == null
            || SecurityContextHolder.getContext().getAuthentication() == null
            || SecurityContextHolder.getContext().getAuthentication()
                instanceof AnonymousAuthenticationToken);
  }

  @Override
  public Object elevate() {
    SecurityContext ret = SecurityContextHolder.getContext();
    SecurityContext context = new SecurityContextImpl();
    // NOTE: We're creating new instance of BackgroundProcessAuthentication
    // because there is setAuthenticated(false) possible
    context.setAuthentication(getAuthentication());
    SecurityContextHolder.setContext(context);
    return ret;
  }

  protected Authentication getAuthentication() {
    if (authentication == null) {
      try {
        User user = userService.getUserByEmail(userEmail);
        List<String> permissions =
            permissionService.findUserPermissionsForSubject(
                SecurityConstants.DOMAIN, user.getUuid(), null);
        UserDetailsImpl userDetails = new UserDetailsImpl(user, "[NO PASSWORD]", permissions, null);
        UsernamePasswordAuthenticationToken ret =
            new UsernamePasswordAuthenticationToken(
                userDetails, "[NO PASSWORD]", userDetails.getAuthorities());
        // NOTE: No details for this kind of authentication
        authentication = ret;
      } catch (Throwable t) {
        throw new RuntimeException("Failed to build Authnetication", t);
      }
    }
    return authentication;
  }

  @Override
  public void deElevate(Object previousContext) {
    if (previousContext == null) {
      SecurityContextHolder.clearContext();
    } else {
      SecurityContextHolder.setContext((SecurityContext) previousContext);
    }
  }
}
