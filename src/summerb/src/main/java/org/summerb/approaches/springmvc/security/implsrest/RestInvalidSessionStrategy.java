package org.summerb.approaches.springmvc.security.implsrest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.session.InvalidSessionStrategy;
import org.summerb.approaches.security.api.dto.NotAuthorizedResult;
import org.summerb.approaches.springmvc.security.SecurityMessageCodes;
import org.summerb.approaches.springmvc.security.apis.JsonResponseWriter;

public class RestInvalidSessionStrategy implements InvalidSessionStrategy {
	private JsonResponseWriter jsonResponseHelper;

	public RestInvalidSessionStrategy() {
		jsonResponseHelper = new JsonResponseHelperGsonImpl();
	}

	public RestInvalidSessionStrategy(JsonResponseWriter jsonResponseHelper) {
		this.jsonResponseHelper = jsonResponseHelper;
	}

	@Override
	public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// create new session, which will result in JSESSIONID coockie reset
		request.getSession();

		// Report that session changed and need to reestablish request
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		jsonResponseHelper.writeResponseBody(new NotAuthorizedResult("anonymous", SecurityMessageCodes.INVALID_SESSION),
				response);
	}

}
