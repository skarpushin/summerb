package org.summerb.spring.security.api;

import java.util.Set;

public interface CurrentUserRolesResolver {

  boolean hasRole(String role);

  boolean hasAnyRole(String... roles);

  boolean hasAnyRole(Set<String> roles);
  
}
