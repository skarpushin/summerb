package org.summerb.approaches.springmvc.security.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.summerb.microservices.users.api.dto.AuthToken;
import org.summerb.microservices.users.api.dto.User;

public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = -1939360452604292858L;

	private final User user;
	private final String passwordHash;
	private final AuthToken authToken;

	private List<GrantedAuthority> userAuthorities;

	public UserDetailsImpl(User user, String passwordHash, List<String> permissions, AuthToken authToken) {
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
