package org.summerb.approaches.springmvc.security.implsrest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.summerb.approaches.springmvc.security.apis.JsonResponseWriter;
import org.summerb.approaches.springmvc.security.dto.LoginResult;
import org.summerb.approaches.springmvc.security.dto.UserDetailsImpl;

public class RestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private JsonResponseWriter jsonResponseHelper;

	public RestAuthenticationSuccessHandler() {
		jsonResponseHelper = new JsonResponseWriterGsonImpl();
	}

	public RestAuthenticationSuccessHandler(JsonResponseWriter jsonResponseHelper) {
		this.jsonResponseHelper = jsonResponseHelper;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		request.getSession(true);
		jsonResponseHelper.writeResponseBody(buildLoginResult(authentication), response);
	}

	private LoginResult buildLoginResult(Authentication authentication) {
		LoginResult ret = new LoginResult();
		ret.setUser(((UserDetailsImpl) authentication.getPrincipal()).getUser());
		// NOTE: rememberMeToken is not written here. It will be captured on the
		// client from HTTP Cookies of response
		return ret;
	}

}
