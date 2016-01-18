package org.summerb.approaches.security.api;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;

public interface SecurityContextResolver<TUser> extends CurrentUserResolver<TUser> {
	SecurityContext resolveSecurityContext();

	Collection<? extends GrantedAuthority> getCurrentUserGlobalPermissions();

	boolean hasRole(String role);

	boolean hasAnyRole(String... roles);
}
