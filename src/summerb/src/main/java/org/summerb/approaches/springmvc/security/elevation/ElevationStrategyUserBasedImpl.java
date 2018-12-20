package org.summerb.approaches.springmvc.security.elevation;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.util.StringUtils;
import org.summerb.approaches.springmvc.security.SecurityConstants;
import org.summerb.approaches.springmvc.security.apis.ElevationStrategy;
import org.summerb.approaches.springmvc.security.dto.UserDetailsImpl;
import org.summerb.microservices.users.api.PermissionService;
import org.summerb.microservices.users.api.UserService;
import org.summerb.microservices.users.api.dto.User;

import com.google.common.base.Preconditions;

/**
 * This impl elevates to BackgroundProcess credentials if current user is
 * missing or ANonymous
 * 
 * @author sergey.karpushin
 *
 */
public class ElevationStrategyUserBasedImpl implements ElevationStrategy, InitializingBean {
	@Autowired
	private UserService userService;
	@Autowired
	private PermissionService permissionService;

	private String userEmail;
	private Authentication authentication;
	private boolean force;

	public ElevationStrategyUserBasedImpl(String userEmail) {
		this(userEmail, false);
	}

	public ElevationStrategyUserBasedImpl(String userEmail, boolean force) {
		Preconditions.checkArgument(StringUtils.hasText(userEmail));
		this.userEmail = userEmail;
		this.force = force;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		getAuthentication(); // eagerly load user
	}

	@Override
	public boolean isElevationRequired() {
		return force || (SecurityContextHolder.getContext() == null
				|| SecurityContextHolder.getContext().getAuthentication() == null
				|| SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
	}

	@Override
	public Object elevate() {
		SecurityContext ret = SecurityContextHolder.getContext();
		SecurityContext context = new SecurityContextImpl();
		// NOTE: We're creating new instance of BackgroundProcessAuthentication
		// because there is setAuthenticated(false) possible
		context.setAuthentication(getAuthentication());
		SecurityContextHolder.setContext(context);
		return ret;
	}

	private Authentication getAuthentication() {
		if (authentication == null) {
			try {
				User user = userService.getUserByEmail(userEmail);
				List<String> permissions = permissionService.findUserPermissionsForSubject(SecurityConstants.DOMAIN,
						user.getUuid(), null);
				UserDetailsImpl userDetails = new UserDetailsImpl(user, "[NO PASSWORD]", permissions, null);
				UsernamePasswordAuthenticationToken ret = new UsernamePasswordAuthenticationToken(userDetails,
						"[NO PASSWORD]", userDetails.getAuthorities());
				// NOTE: No details for this kind of authentication
				authentication = ret;
			} catch (Throwable t) {
				throw new RuntimeException("Failed to build Authnetication", t);
			}
		}
		return authentication;
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
