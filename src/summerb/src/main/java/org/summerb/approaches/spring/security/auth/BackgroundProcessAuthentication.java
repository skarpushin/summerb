package org.summerb.approaches.spring.security.auth;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.summerb.approaches.spring.security.SecurityConstants;

public class BackgroundProcessAuthentication implements Authentication {
	private static final long serialVersionUID = 3710514197842955814L;

	private String origin;

	private static List<? extends GrantedAuthority> AUTHORITIES = Arrays
			.asList(new SimpleGrantedAuthority(SecurityConstants.ROLE_BACKGROUND_PROCESS));
	private boolean authenticated = true;

	/**
	 * 
	 * @param origin
	 *            some string which is probably suppose to clarify what is the
	 *            origin of that authentication. Not used for any logic - just
	 *            for tracing/debugging purposes
	 */
	public BackgroundProcessAuthentication(String origin) {
		this.origin = origin;
	}

	@Override
	public String getName() {
		return origin;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AUTHORITIES;
	}

	@Override
	public Object getCredentials() {
		return origin;
	}

	@Override
	public Object getDetails() {
		return origin;
	}

	@Override
	public Object getPrincipal() {
		return origin;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		throw new IllegalStateException("Opearion is not supported");
	}

}
