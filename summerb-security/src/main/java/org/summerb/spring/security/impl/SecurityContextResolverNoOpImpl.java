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
package org.summerb.spring.security.impl;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.summerb.security.api.CurrentUserNotFoundException;
import org.summerb.spring.security.api.SecurityContextResolver;

@SuppressWarnings("rawtypes")
public class SecurityContextResolverNoOpImpl implements SecurityContextResolver {

  @Override
  public UserDetails getUser() throws CurrentUserNotFoundException {
    return null;
  }

  @Override
  public String getUserUuid() throws CurrentUserNotFoundException {
    return null;
  }

  @Override
  public SecurityContext resolveSecurityContext() {
    return null;
  }

  @Override
  public Collection<? extends GrantedAuthority> getCurrentUserGlobalPermissions() {
    return null;
  }

  @Override
  public boolean hasRole(String role) {
    return false;
  }

  @Override
  public boolean hasAnyRole(String... roles) {
    return false;
  }
}
