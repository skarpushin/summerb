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

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.summerb.security.elevation.api.ElevationStrategy;
import org.summerb.users.api.dto.User;
import org.summerb.webappboilerplate.security.impls.UserDetailsImpl;

public class ElevationStrategyRunAsUserImpl<TUser extends User> implements ElevationStrategy {
  protected TUser user;
  protected List<String> userGlobalPermissions;

  public ElevationStrategyRunAsUserImpl(TUser user, List<String> userGlobalPermissions) {
    this.user = user;
    this.userGlobalPermissions = userGlobalPermissions;
  }

  @Override
  public boolean isElevationRequired() {
    // TBD: Check if current user matches requested user then elevation not
    // requried
    return true;
  }

  @Override
  public Object elevate() {
    SecurityContext ret = SecurityContextHolder.getContext();
    SecurityContext context = new SecurityContextImpl();
    context.setAuthentication(buildAuthForUser());
    SecurityContextHolder.setContext(context);
    return ret;
  }

  protected Authentication buildAuthForUser() {
    UserDetailsImpl userDetails = new UserDetailsImpl(user, "", userGlobalPermissions, null);
    UsernamePasswordAuthenticationToken ret =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    ret.setDetails(null);
    return ret;
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
