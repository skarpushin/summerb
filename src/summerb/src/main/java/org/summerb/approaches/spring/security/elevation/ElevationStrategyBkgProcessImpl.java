package org.summerb.approaches.spring.security.elevation;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.summerb.approaches.security.elevation.api.ElevationStrategy;
import org.summerb.approaches.spring.security.auth.BackgroundProcessAuthentication;

/**
 * This impl elevates to BackgroundProcess credentials if current user is
 * missing or ANonymous
 * 
 * @author sergey.karpushin
 *
 */
public class ElevationStrategyBkgProcessImpl implements ElevationStrategy {
	private BackgroundProcessAuthentication auth = new BackgroundProcessAuthentication("FallbackToBkgAuthProxy");

	@Override
	public boolean isElevationRequired() {
		return SecurityContextHolder.getContext() == null
				|| SecurityContextHolder.getContext().getAuthentication() == null
				|| SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken;
	}

	@Override
	public Object elevate() {
		SecurityContext ret = SecurityContextHolder.getContext();
		SecurityContext context = new SecurityContextImpl();
		// NOTE: We're creating new instance of BackgroundProcessAuthentication
		// because there is setAuthenticated(false) possible
		context.setAuthentication(auth);
		SecurityContextHolder.setContext(context);
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
