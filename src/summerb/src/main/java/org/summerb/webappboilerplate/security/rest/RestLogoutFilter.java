package org.summerb.webappboilerplate.security.rest;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.filter.GenericFilterBean;
import org.summerb.utils.json.JsonResponseWriter;
import org.summerb.utils.json.JsonResponseWriterGsonImpl;

public class RestLogoutFilter extends GenericFilterBean {
	private List<LogoutHandler> handlers;
	private String triggerPath = "/rest/logout";
	private JsonResponseWriter jsonResponseHelper;

	public RestLogoutFilter() {
		jsonResponseHelper = new JsonResponseWriterGsonImpl();
	}

	public RestLogoutFilter(JsonResponseWriter jsonResponseHelper) {
		this.jsonResponseHelper = jsonResponseHelper;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (requiresLogout(request, response)) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			if (logger.isDebugEnabled()) {
				logger.debug("Logging out user '" + auth + "' and transferring to logout destination");
			}

			for (LogoutHandler handler : handlers) {
				handler.logout(request, response, auth);
			}

			response.setStatus(200);
			jsonResponseHelper.writeResponseBody("Logged out", response);
			return;
		}

		chain.doFilter(request, response);
	}

	private boolean requiresLogout(HttpServletRequest request, HttpServletResponse response) {
		if (triggerPath.equalsIgnoreCase(request.getServletPath())) {
			return true;
		}
		return false;
	}

	public List<LogoutHandler> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<LogoutHandler> handlers) {
		this.handlers = handlers;
	}

	public String getTriggerPath() {
		return triggerPath;
	}

	public void setTriggerPath(String basePath) {
		this.triggerPath = basePath;
	}

}
