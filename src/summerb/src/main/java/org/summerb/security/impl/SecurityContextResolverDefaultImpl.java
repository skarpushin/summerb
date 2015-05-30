package org.summerb.security.impl;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.summerb.security.api.SecurityContextResolver;
import org.summerb.security.api.exceptions.CurrentUserNotFoundException;

public class SecurityContextResolverDefaultImpl implements SecurityContextResolver {

	@Override
	public UserDetails getUser() throws CurrentUserNotFoundException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			throw new CurrentUserNotFoundException();
		}

		Object principal = authentication.getPrincipal();
		if (principal instanceof UserDetails) {
			return (UserDetails) principal;
		}

		throw new CurrentUserNotFoundException();
	}

	@Override
	public String getUserUuid() throws CurrentUserNotFoundException {
		return getUser().getUsername();
	}

	@Override
	public SecurityContext resolveSecurityContext() {
		return SecurityContextHolder.getContext();
	}

	@Override
	public Collection<? extends GrantedAuthority> getCurrentUserGlobalPermissions() {
		return getUser().getAuthorities();
	}

	@Override
	public boolean hasRole(String permission) {
		if (permission == null) {
			return false;
		}
		for (GrantedAuthority a : getUser().getAuthorities()) {
			if (permission.equals(a.getAuthority())) {
				return true;
			}
		}
		return false;
	}

}
