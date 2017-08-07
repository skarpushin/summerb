package org.summerb.approaches.springmvc.security.apis;

import javax.servlet.http.HttpServletRequest;

/**
 * Impl of this interface is to make decision if remember me services are
 * requested
 * 
 * @author sergeyk
 * 
 */
public interface RememberMeRequestedStrategy {
	boolean isRememberMeRequested(HttpServletRequest request);
}
