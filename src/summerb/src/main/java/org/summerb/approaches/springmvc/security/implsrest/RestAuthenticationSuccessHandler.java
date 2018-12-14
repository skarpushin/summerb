package org.summerb.approaches.springmvc.security.implsrest;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.summerb.approaches.springmvc.security.apis.JsonResponseWriter;
import org.summerb.approaches.springmvc.security.dto.LoginResult;
import org.summerb.approaches.springmvc.security.dto.UserDetailsImpl;

public class RestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private JsonResponseWriter jsonResponseHelper;

	/**
	 * Should be the same one as used by
	 * {@link org.springframework.security.web.access.ExceptionTranslationFilter}.
	 * 
	 * And
	 * {@link org.springframework.security.web.savedrequest.RequestCacheAwareFilter}
	 * if used
	 */
	private RequestCache requestCache = new NullRequestCache();

	public RestAuthenticationSuccessHandler() {
		jsonResponseHelper = new JsonResponseWriterGsonImpl();
	}

	public RestAuthenticationSuccessHandler(JsonResponseWriter jsonResponseHelper) {
		this.jsonResponseHelper = jsonResponseHelper;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		HttpSession session = request.getSession(true);
		LoginResult ret = new LoginResult();
		ret.setUser(((UserDetailsImpl) authentication.getPrincipal()).getUser());
		addCustomAttrsIfAny(session, request, response, ret);
		jsonResponseHelper.writeResponseBody(ret, response);
	}

	protected void addCustomAttrsIfAny(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			LoginResult ret) {

		SavedRequest savedRequest = requestCache.getRequest(request, response);
		if (savedRequest != null) {
			ret.setAttributes(new HashMap<>());
			ret.getAttributes().put(LoginResult.ATTR_REDIRECT_TO, savedRequest.getRedirectUrl());

			requestCache.removeRequest(request, response);
		}
	}

	public RequestCache getRequestCache() {
		return requestCache;
	}

	@Autowired(required = false)
	public void setRequestCache(RequestCache requestCache) {
		this.requestCache = requestCache;
	}
}
