package org.summerb.webappboilerplate.security.impls;

import javax.servlet.http.HttpServletRequest;

import org.summerb.webappboilerplate.security.apis.RememberMeRequestedStrategy;
import org.summerb.webappboilerplate.security.dto.LoginParams;

/**
 * Our way - how to determine if remember me is requested
 * 
 * @author sergeyk
 * 
 */
public class RememberMeRequestedStrategyImpl implements RememberMeRequestedStrategy {
	private String rememberMeParameter = "_spring_security_remember_me";

	@Override
	public boolean isRememberMeRequested(HttpServletRequest request) {
		boolean isJson = request.getContentType() != null && request.getContentType().contains("application/json");
		if (isJson) {
			return Boolean.TRUE.toString().equals(request.getHeader(LoginParams.HEADER_REMEMBER_ME));
		}

		return isFormAuthRequestForRememberMe(request);
	}

	private boolean isFormAuthRequestForRememberMe(HttpServletRequest request) {
		String paramValue = request.getParameter(rememberMeParameter);

		if (paramValue != null) {
			if (paramValue.equalsIgnoreCase("true") || paramValue.equalsIgnoreCase("on")
					|| paramValue.equalsIgnoreCase("yes") || paramValue.equals("1")) {
				return true;
			}
		}

		return false;
	}

	public String getRememberMeParameter() {
		return rememberMeParameter;
	}

	public void setRememberMeParameter(String rememberMeParameter) {
		this.rememberMeParameter = rememberMeParameter;
	}
}
