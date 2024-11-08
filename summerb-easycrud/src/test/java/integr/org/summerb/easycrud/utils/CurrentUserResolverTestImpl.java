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
package integr.org.summerb.easycrud.utils;

import java.util.Arrays;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.summerb.security.api.CurrentUserDetailsResolver;
import org.summerb.security.api.CurrentUserUuidResolver;
import org.summerb.security.api.Roles;
import org.summerb.spring.security.api.CurrentUserRolesResolver;

public class CurrentUserResolverTestImpl
    implements CurrentUserUuidResolver, CurrentUserDetailsResolver<User>, CurrentUserRolesResolver {
  public User user;
  public User user1;
  public User user2;

  public CurrentUserResolverTestImpl() {
    user1 = new User("user1", "1", Arrays.asList(new SimpleGrantedAuthority(Roles.ROLE_USER)));
    user2 = new User("user2", "2", Arrays.asList(new SimpleGrantedAuthority(Roles.ROLE_USER)));
    user = user1;
  }

  @Override
  public User getUser() {
    return user;
  }

  @Override
  public String getUserUuid() {
    return user.getUsername();
  }

  @Override
  public boolean hasRole(String role) {
    return user.getAuthorities().stream().anyMatch(x -> String.valueOf(x).equalsIgnoreCase(role));
  }

  @Override
  public boolean hasAnyRole(String... roles) {
    return Arrays.stream(roles).anyMatch(this::hasRole);
  }

  @Override
  public boolean hasAnyRole(Set<String> roles) {
    return roles.stream().anyMatch(this::hasRole);
  }
}
