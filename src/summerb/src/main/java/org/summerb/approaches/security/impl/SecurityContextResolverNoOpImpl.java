package org.summerb.approaches.security.impl;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.summerb.approaches.security.api.CurrentUserNotFoundException;
import org.summerb.approaches.security.api.SecurityContextResolver;

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
