package org.summerb.approaches.springmvc.security.implsrest;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.summerb.approaches.jdbccrud.common.DtoBase;
import org.summerb.approaches.security.api.CurrentUserNotFoundException;
import org.summerb.approaches.security.api.dto.NotAuthorizedResult;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.springmvc.security.SecurityMessageCodes;
import org.summerb.approaches.springmvc.security.apis.JsonResponseWriter;
import org.summerb.utils.exceptions.ExceptionUtils;
import org.summerb.utils.exceptions.dto.ExceptionInfo;
import org.summerb.utils.exceptions.dto.GenericServerErrorResult;
import org.summerb.utils.exceptions.translator.ExceptionTranslator;

public class RestExceptionTranslator extends GenericFilterBean {
	private Logger log = Logger.getLogger(getClass());

	private AuthenticationTrustResolver authenticationTrustResolver;
	private JsonResponseWriter jsonResponseHelper;

	private ExceptionTranslator exceptionTranslator;

	public RestExceptionTranslator() {
		jsonResponseHelper = new JsonResponseWriterGsonImpl();
		authenticationTrustResolver = new AuthenticationTrustResolverImpl();
	}

	public RestExceptionTranslator(JsonResponseWriter jsonResponseHelper,
			AuthenticationTrustResolver authenticationTrustResolver) {
		this.jsonResponseHelper = jsonResponseHelper;
		this.authenticationTrustResolver = authenticationTrustResolver;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		try {
			chain.doFilter(request, response);
			logger.trace("Chain processed normally");
		} catch (Exception ex) {
			log.warn("Unhandled exception while processing REST query", ex);
			// THINK: Not sure if this is that corect implementation. Because
			// we're supposed to forward exception here so it will be handled by
			// Controller... But this is rest path... If exception is
			// unhandled... there is no page we want to render.
			DtoBase result = determineFailureResult(ex);
			response.setStatus(result instanceof NotAuthorizedResult ? HttpServletResponse.SC_UNAUTHORIZED
					: HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			jsonResponseHelper.writeResponseBody(result, response);
		}
	}

	private DtoBase determineFailureResult(Exception ex) {
		// TODO: Why we do not handle FVE here ?

		NotAuthorizedException naex = ExceptionUtils.findExceptionOfType(ex, NotAuthorizedException.class);
		if (naex != null) {
			return naex.getResult();
		}

		AuthenticationException ae = ExceptionUtils.findExceptionOfType(ex, AuthenticationException.class);
		if (ae != null) {
			// NOTE: See how we did that in AuthenticationFailureHandlerImpl...
			// Looks liek we need to augment our custom RestLoginFilter so it
			// will put username to request
			return new NotAuthorizedResult("(username not resolved)", SecurityMessageCodes.AUTH_FATAL);
		}

		AccessDeniedException ade = ExceptionUtils.findExceptionOfType(ex, AccessDeniedException.class);
		if (ade != null) {
			if (authenticationTrustResolver.isAnonymous(SecurityContextHolder.getContext().getAuthentication())) {
				return new NotAuthorizedResult(getCurrentUser(null), SecurityMessageCodes.LOGIN_REQUIRED);
			}
			return new NotAuthorizedResult(getCurrentUser(null), SecurityMessageCodes.ACCESS_DENIED);
		}

		CurrentUserNotFoundException cunfe = ExceptionUtils.findExceptionOfType(ex, CurrentUserNotFoundException.class);
		if (cunfe != null) {
			return new NotAuthorizedResult(getCurrentUser(null), SecurityMessageCodes.LOGIN_REQUIRED);
		}

		return new GenericServerErrorResult(exceptionTranslator.buildUserMessage(ex, LocaleContextHolder.getLocale()),
				new ExceptionInfo(ex));
	}

	protected String getCurrentUser(Authentication optionalAuthentication) {
		Authentication auth = optionalAuthentication;
		if (auth == null) {
			auth = SecurityContextHolder.getContext().getAuthentication();
		}

		if (auth != null) {
			return auth.getName();
		}

		return SecurityMessageCodes.ANONYMOUS;
	}

	public AuthenticationTrustResolver getAuthenticationTrustResolver() {
		return authenticationTrustResolver;
	}

	public void setAuthenticationTrustResolver(AuthenticationTrustResolver authenticationTrustResolver) {
		this.authenticationTrustResolver = authenticationTrustResolver;
	}

	public ExceptionTranslator getExceptionTranslator() {
		return exceptionTranslator;
	}

	@Autowired
	public void setExceptionTranslator(ExceptionTranslator exceptionTranslator) {
		this.exceptionTranslator = exceptionTranslator;
	}
}