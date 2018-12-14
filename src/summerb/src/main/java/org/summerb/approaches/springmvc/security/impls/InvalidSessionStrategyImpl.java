package org.summerb.approaches.springmvc.security.impls;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.summerb.approaches.springmvc.security.apis.SecurityActionsUrlsProvider;
import org.summerb.approaches.springmvc.security.mvc.SecurityActionsUrlsProviderDefaultImpl;

/**
 * That impl was created to be able to remember url that was initially requested
 * by user so that we can redirect there after successful authentication
 * 
 * @author sergeyk
 *
 */
public class InvalidSessionStrategyImpl implements InvalidSessionStrategy, InitializingBean {
	private Logger log = Logger.getLogger(getClass());

	private RedirectStrategy redirectStrategy;
	private SecurityActionsUrlsProvider securityActionsUrlsProvider;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (redirectStrategy == null) {
			redirectStrategy = new DefaultRedirectStrategy();
		}
		if (securityActionsUrlsProvider == null) {
			securityActionsUrlsProvider = new SecurityActionsUrlsProviderDefaultImpl();
		}
	}

	@Override
	public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if (log.isDebugEnabled()) {
			log.debug(
					"Looks like current session is invalid. Will clear JSESSIONID cookie and redirect user to the same page");
		}

		request.getSession();

		// Ask user browser to just redraw current page
		String requestedResource = getRequestedResourceUrl(request);
		String redirecctTo = securityActionsUrlsProvider.getInvalidSession() + "?requestedUrl="
				+ URLEncoder.encode(requestedResource, "UTF-8");
		redirectStrategy.sendRedirect(request, response, redirecctTo);
	}

	private String getRequestedResourceUrl(HttpServletRequest request) {
		return request.getRequestURL().toString()
				+ ((request.getQueryString() == null) ? "" : "?" + request.getQueryString());
	}

	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

	public RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}

	public SecurityActionsUrlsProvider getSecurityActionsUrlsProvider() {
		return securityActionsUrlsProvider;
	}

	@Autowired(required = false)
	public void setSecurityActionsUrlsProvider(SecurityActionsUrlsProvider securityActionsUrlsProvider) {
		this.securityActionsUrlsProvider = securityActionsUrlsProvider;
	}

}
