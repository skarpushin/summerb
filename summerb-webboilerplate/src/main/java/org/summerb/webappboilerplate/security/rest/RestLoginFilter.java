/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.webappboilerplate.security.rest;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.filter.GenericFilterBean;

public class RestLoginFilter extends GenericFilterBean implements ApplicationEventPublisherAware {
	private static final String AUTHORIZATION_PREFIX = "Basic ";

	private ApplicationEventPublisher eventPublisher;
	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
	private AuthenticationManager authenticationManager;
	private RememberMeServices rememberMeServices = new NullRememberMeServices();
	private SessionAuthenticationStrategy sessionAuthenticationStrategy = new NullAuthenticatedSessionStrategy();
	private AuthenticationSuccessHandler authenticationSuccessHandler;
	private AuthenticationFailureHandler authenticationFailureHandler;

	private String triggerPath = "/rest/login";
	private String authorizationHeaderName = "X-" + HttpHeaders.AUTHORIZATION;
	private String credentialsCharset = "UTF-8";

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (!requiresAuthentication(request, response)) {
			chain.doFilter(request, response);
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Request is to process authentication");
		}

		Authentication authResult;
		try {
			authResult = attemptAuthentication(request, response);
			if (authResult == null) {
				return;
			}
			sessionAuthenticationStrategy.onAuthentication(authResult, request, response);
		} catch (AuthenticationException failed) {
			unsuccessfulAuthentication(request, response, failed);
			return;
		}

		successfulAuthentication(request, response, authResult);
	}

	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			Authentication authResult) throws IOException, ServletException {
		if (logger.isDebugEnabled()) {
			logger.debug("Authentication success. Updating SecurityContextHolder to contain: " + authResult);
		}

		SecurityContextHolder.getContext().setAuthentication(authResult);
		rememberMeServices.loginSuccess(request, response, authResult);

		// Fire event
		if (this.eventPublisher != null) {
			eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
		}

		authenticationSuccessHandler.onAuthenticationSuccess(request, response, authResult);
	}

	private Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		String authorizationHeader = request.getHeader(authorizationHeaderName);
		String[] tokens = extractAndDecodeHeader(authorizationHeader, request);
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(tokens[0], tokens[1]);
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
		return authenticationManager.authenticate(authRequest);
	}

	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		SecurityContextHolder.clearContext();

		if (logger.isDebugEnabled()) {
			logger.debug("Authentication request failed: " + failed.toString());
			logger.debug("Updated SecurityContextHolder to contain null Authentication");
			logger.debug("Delegating to authentication failure handler" + authenticationFailureHandler);
		}

		rememberMeServices.loginFail(request, response);

		authenticationFailureHandler.onAuthenticationFailure(request, response, failed);
	}

	private boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
		String header = request.getHeader(authorizationHeaderName);
		if (header == null || !header.startsWith(AUTHORIZATION_PREFIX)) {
			return false;
		}
		if (!triggerPath.equalsIgnoreCase(request.getServletPath())) {
			return false;
		}
		return true;
	}

	/**
	 * Decodes the header into a username and password.
	 * 
	 * @throws BadCredentialsException
	 *             if the Basic header is not present or is not valid Base64
	 */
	private String[] extractAndDecodeHeader(String header, HttpServletRequest request) {
		try {
			byte[] base64Token = header.substring(AUTHORIZATION_PREFIX.length()).getBytes("UTF-8");
			byte[] decoded;
			try {
				decoded = Base64.decode(base64Token);
			} catch (IllegalArgumentException e) {
				throw new BadCredentialsException("Failed to decode basic authentication token");
			}

			String token = new String(decoded, getCredentialsCharset(request));

			int delim = token.indexOf(":");

			if (delim == -1) {
				throw new BadCredentialsException("Invalid basic authentication token");
			}
			return new String[] { token.substring(0, delim), token.substring(delim + 1) };
		} catch (IOException ioe) {
			throw new RuntimeException("Failed to decode auth tokens");
		}
	}

	protected String getCredentialsCharset(HttpServletRequest httpRequest) {
		return credentialsCharset;
	}

	public void setCredentialsCharset(String credentialsCharset) {
		this.credentialsCharset = credentialsCharset;
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}

	public AuthenticationDetailsSource<HttpServletRequest, ?> getAuthenticationDetailsSource() {
		return authenticationDetailsSource;
	}

	public void setAuthenticationDetailsSource(
			AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
		this.authenticationDetailsSource = authenticationDetailsSource;
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	@Required
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public RememberMeServices getRememberMeServices() {
		return rememberMeServices;
	}

	public void setRememberMeServices(RememberMeServices rememberMeServices) {
		this.rememberMeServices = rememberMeServices;
	}

	public SessionAuthenticationStrategy getSessionAuthenticationStrategy() {
		return sessionAuthenticationStrategy;
	}

	public void setSessionAuthenticationStrategy(SessionAuthenticationStrategy sessionStrategy) {
		this.sessionAuthenticationStrategy = sessionStrategy;
	}

	public AuthenticationSuccessHandler getAuthenticationSuccessHandler() {
		return authenticationSuccessHandler;
	}

	public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
		this.authenticationSuccessHandler = successHandler;
	}

	public AuthenticationFailureHandler getAuthenticationFailureHandler() {
		return authenticationFailureHandler;
	}

	public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
		this.authenticationFailureHandler = failureHandler;
	}

	public String getTriggerPath() {
		return triggerPath;
	}

	public void setTriggerPath(String triggerPath) {
		this.triggerPath = triggerPath;
	}

	public String getAuthorizationHeaderName() {
		return authorizationHeaderName;
	}

	public void setAuthorizationHeaderName(String authorizationHeaderName) {
		this.authorizationHeaderName = authorizationHeaderName;
	}

}
