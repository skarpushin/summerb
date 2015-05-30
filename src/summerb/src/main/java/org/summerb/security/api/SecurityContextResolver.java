package org.summerb.security.api;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;

public interface SecurityContextResolver extends CurrentUserResolver {
	SecurityContext resolveSecurityContext();

	Collection<? extends GrantedAuthority> getCurrentUserGlobalPermissions();

	boolean hasRole(String permission);
}
