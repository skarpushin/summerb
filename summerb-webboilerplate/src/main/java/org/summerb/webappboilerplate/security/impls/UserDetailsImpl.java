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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.summerb.users.api.dto.AuthToken;
import org.summerb.users.api.dto.User;

public class UserDetailsImpl implements UserDetails {
  protected static final long serialVersionUID = -1939360452604292858L;

  protected final User user;
  protected final String passwordHash;
  protected final AuthToken authToken;

  protected List<GrantedAuthority> userAuthorities;

  public UserDetailsImpl(
      User user, String passwordHash, List<String> permissions, AuthToken authToken) {
    this.user = user;
    this.passwordHash = passwordHash;
    this.authToken = authToken;

    userAuthorities = new ArrayList<GrantedAuthority>();
    for (String permission : permissions) {
      userAuthorities.add(new SimpleGrantedAuthority(permission));
    }
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return userAuthorities;
  }

  @Override
  public String getPassword() {
    return passwordHash;
  }

  @Override
  public String getUsername() {
    return getUser().getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public User getUser() {
    return user;
  }

  public String getEmail() {
    return user.getEmail();
  }

  @Override
  public String toString() {
    return user == null ? super.toString() : user.getEmail();
  }

  public AuthToken getAuthToken() {
    return authToken;
  }
}
