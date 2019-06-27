package org.summerb.webappboilerplate.security.impls;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

/**
 * This impl was created to pass userName to the login form so that user don't
 * have to enter it after failed previos login attempt
 * 
 * @author sergey.k
 * 
 */
public class AuthenticationFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {
	private static final String ATTR_LAST_FAILED_USER_NAME = "userName";
	private String defaultFailureUrl;

	@Autowired
	private UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter;

	public AuthenticationFailureHandlerImpl() {

	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String usernameParameter = usernamePasswordAuthenticationFilter.getUsernameParameter();
		String userName = request.getParameter(usernameParameter);
		saveException(request, exception);
		if (StringUtils.hasText(userName)) {
			getRedirectStrategy().sendRedirect(request, response,
					defaultFailureUrl + "?" + ATTR_LAST_FAILED_USER_NAME + "=" + userName);
		} else {
			getRedirectStrategy().sendRedirect(request, response, defaultFailureUrl);
		}
	}

	@Override
	public void setDefaultFailureUrl(String defaultFailureUrl) {
		this.defaultFailureUrl = defaultFailureUrl;
		super.setDefaultFailureUrl(defaultFailureUrl);
	}

}
