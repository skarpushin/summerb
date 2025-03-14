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
package org.summerb.spring.security.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.summerb.security.api.CurrentUserNotFoundException;
import org.summerb.spring.security.api.SecurityContextResolver;

public abstract class SecurityContextResolverAbstract<TUser>
    implements SecurityContextResolver<TUser> {

  @Override
  public SecurityContext resolveSecurityContext() {
    return SecurityContextHolder.getContext();
  }

  @Override
  public TUser getUser() throws CurrentUserNotFoundException {
    Authentication authentication = resolveSecurityContext().getAuthentication();
    if (authentication == null) {
      throw new CurrentUserNotFoundException();
    }

    Object principal = authentication.getPrincipal();
    if (principal instanceof UserDetails) {
      return getUserFromUserDetails((UserDetails) principal);
    }

    throw new CurrentUserNotFoundException();
  }

  protected abstract TUser getUserFromUserDetails(UserDetails principal);

  public Collection<? extends GrantedAuthority> getCurrentUserGlobalPermissions() {
    Authentication authentication = resolveSecurityContext().getAuthentication();
    if (authentication == null) {
      return Collections.emptyList();
    }

    if (authentication.getAuthorities() == null) {
      return Collections.emptyList();
    }

    return authentication.getAuthorities();
  }

  @Override
  public boolean hasRole(String role) {
    if (role == null) {
      return false;
    }
    for (GrantedAuthority a : getCurrentUserGlobalPermissions()) {
      if (role.equalsIgnoreCase(a.getAuthority())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean hasAnyRole(String... roles) {
    for (String role : roles) {
      if (hasRole(role)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean hasAnyRole(Set<String> roles) {
    for (String role : roles) {
      if (hasRole(role)) {
        return true;
      }
    }
    return false;
  }
}
