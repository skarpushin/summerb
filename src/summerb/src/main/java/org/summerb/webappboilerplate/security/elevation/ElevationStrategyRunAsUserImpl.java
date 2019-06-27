package org.summerb.webappboilerplate.security.elevation;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.summerb.approaches.security.elevation.api.ElevationStrategy;
import org.summerb.microservices.users.api.dto.User;
import org.summerb.webappboilerplate.security.impls.UserDetailsImpl;

public class ElevationStrategyRunAsUserImpl<TUser extends User> implements ElevationStrategy {
	private TUser user;
	private List<String> userGlobalPermissions;

	public ElevationStrategyRunAsUserImpl(TUser user, List<String> userGlobalPermissions) {
		this.user = user;
		this.userGlobalPermissions = userGlobalPermissions;
	}

	@Override
	public boolean isElevationRequired() {
		// TBD: Check if current user matches requested user then elevation not
		// requried
		return true;
	}

	@Override
	public Object elevate() {
		SecurityContext ret = SecurityContextHolder.getContext();
		SecurityContext context = new SecurityContextImpl();
		context.setAuthentication(buildAuthForUser());
		SecurityContextHolder.setContext(context);
		return ret;
	}

	private Authentication buildAuthForUser() {
		UserDetailsImpl userDetails = new UserDetailsImpl(user, "", userGlobalPermissions, null);
		UsernamePasswordAuthenticationToken ret = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
		ret.setDetails(null);
		return ret;
	}

	@Override
	public void deElevate(Object previousContext) {
		if (previousContext == null) {
			SecurityContextHolder.clearContext();
		} else {
			SecurityContextHolder.setContext((SecurityContext) previousContext);
		}
	}
}
