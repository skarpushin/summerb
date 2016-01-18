package org.summerb.approaches.security.impl;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.summerb.approaches.security.api.CurrentUserNotFoundException;
import org.summerb.approaches.security.api.SecurityContextResolver;

public abstract class SecurityContextResolverAbstract<TUser> implements SecurityContextResolver<TUser> {

	@Override
	public SecurityContext resolveSecurityContext() {
		return SecurityContextHolder.getContext();
	}

	@Override
	public TUser getUser() throws CurrentUserNotFoundException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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

	@Override
	public Collection<? extends GrantedAuthority> getCurrentUserGlobalPermissions() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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

}
