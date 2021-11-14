/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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
package org.summerb.webappboilerplate.utils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.util.NestedServletException;
import org.summerb.security.api.Roles;
import org.summerb.security.api.dto.NotAuthorizedResult;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.spring.security.SecurityMessageCodes;
import org.summerb.spring.security.api.SecurityContextResolver;
import org.summerb.utils.exceptions.ExceptionUtils;
import org.summerb.utils.exceptions.translator.ExceptionTranslator;
import org.summerb.utils.exceptions.translator.ExceptionTranslatorLegacyImpl;
import org.summerb.validation.FieldValidationException;
import org.summerb.webappboilerplate.Views;
import org.summerb.webappboilerplate.controllers.ControllerBase;
import org.summerb.webappboilerplate.model.MessageSeverity;
import org.summerb.webappboilerplate.model.PageMessage;
import org.summerb.webappboilerplate.model.ValidationErrorsVm;
import org.summerb.webappboilerplate.security.rest.RestExceptionTranslator;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;

/**
 * Legacy impl of {@link ControllerExceptionHandlerStrategy}
 * 
 * @deprecated don't use this impl. It's cumbersome and confusing MVC and REST
 *             API approaches. It handles REST API exceptions as well while it
 *             should be handled by {@link RestExceptionTranslator}
 * 
 * @author sergeyk
 *
 */
@Deprecated
public class ControllerExceptionHandlerStrategyLegacyImpl
		implements InitializingBean, ControllerExceptionHandlerStrategy, ApplicationContextAware {
	protected final Logger log = LogManager.getLogger(getClass());

	private Gson gson;
	private MappingJackson2JsonView jsonView;
	private SecurityContextResolver<?> securityContextResolver;
	private ExceptionTranslator exceptionTranslator;
	private ApplicationContext applicationContext;

	public ControllerExceptionHandlerStrategyLegacyImpl() {
	}

	public ControllerExceptionHandlerStrategyLegacyImpl(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Preconditions.checkState(applicationContext != null, "applicationContext required");

		if (jsonView == null) {
			jsonView = new MappingJackson2JsonView();
		}
		if (gson == null) {
			gson = new Gson();
		}
		if (exceptionTranslator == null) {
			// log.info("exceptionTranslator is not set, will use Legacy impl");
			exceptionTranslator = new ExceptionTranslatorLegacyImpl(applicationContext);
		}
	}

	@Override
	public ModelAndView handleUnexpectedControllerException(Throwable ex, HttpServletRequest req,
			HttpServletResponse res) {
		boolean isAcceptJson = req.getHeader("Accept") != null
				&& req.getHeader("Accept").startsWith("application/json");
		boolean isContentTypeJson = req.getContentType() != null && req.getContentType().startsWith("application/json");

		boolean isJsonOutputRequired = isAcceptJson || isContentTypeJson;

		Throwable contained = ex instanceof NestedServletException && ex.getCause() != null ? ex.getCause() : ex;
		return isJsonOutputRequired ? buildJsonError(contained, req, res) : buildHtmlError(contained);
	}

	protected ModelAndView buildHtmlError(Throwable ex) {
		if (securityContextResolver != null
				&& (ex instanceof AccessDeniedException && !securityContextResolver.hasRole(Roles.ROLE_USER))) {
			throw new IllegalArgumentException("Exception will not be handled by default exception handler: " + ex);
		}
		log.error("Exception occured", ex);

		ModelAndView ret = new ModelAndView(Views.ERROR_UNEXPECTED_CLARIFIED);
		String msg = exceptionTranslator.buildUserMessage(ex, LocaleContextHolder.getLocale());
		if (!StringUtils.hasText(msg)) {
			msg = ExceptionUtils.getAllMessagesRaw(ex);
		}

		ControllerBase.addPageMessage(ret.getModel(), new PageMessage(msg, MessageSeverity.Danger));
		ret.getModel().put(ControllerBase.ATTR_EXCEPTION, msg);
		ret.getModel().put(ControllerBase.ATTR_EXCEPTION_STACKTRACE, ExceptionUtils.getThrowableStackTraceAsString(ex));
		return ret;
	}

	/**
	 * This peace of crap needs to be removed. Because in case of JSON it's rest
	 * API, there is no place for {@link ModelAndView}. Response should be pure JSON
	 * content.
	 * 
	 * So instead of implementing it here it's better to just re-throw exception and
	 * let {@link RestExceptionTranslator} handle it and gracefully convert it into
	 * json description of error happened
	 */
	protected ModelAndView buildJsonError(Throwable ex, HttpServletRequest req, HttpServletResponse res) {
		String msg = exceptionTranslator.buildUserMessage(ex, LocaleContextHolder.getLocale());

		NotAuthorizedException nae;
		FieldValidationException fve;
		AccessDeniedException ade;
		boolean translateAuthExc = Boolean.TRUE
				.equals(Boolean.valueOf(req.getHeader(RestExceptionTranslator.X_TRANSLATE_AUTHORIZATION_ERRORS)));
		if ((nae = ExceptionUtils.findExceptionOfType(ex, NotAuthorizedException.class)) != null) {
			NotAuthorizedResult naeResult = nae.getResult();
			res.setStatus(isAnonymous() ? HttpServletResponse.SC_UNAUTHORIZED : HttpServletResponse.SC_FORBIDDEN);
			if (translateAuthExc) {
				return new ModelAndView(jsonView, ControllerBase.ATTR_EXCEPTION, msg);
			} else {
				respondWithJson(naeResult, res);
				return null;
			}
		} else if ((ade = ExceptionUtils.findExceptionOfType(ex, AccessDeniedException.class)) != null) {
			res.setStatus(isAnonymous() ? HttpServletResponse.SC_UNAUTHORIZED : HttpServletResponse.SC_FORBIDDEN);
			if (translateAuthExc) {
				return new ModelAndView(jsonView, ControllerBase.ATTR_EXCEPTION, msg);
			} else {
				respondWithJson(new NotAuthorizedResult(getCurrentUser(), SecurityMessageCodes.ACCESS_DENIED), res);
				return null;
			}
		} else if ((fve = ExceptionUtils.findExceptionOfType(ex, FieldValidationException.class)) != null) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			ValidationErrorsVm vepm = new ValidationErrorsVm(fve.getErrors());
			return new ModelAndView(jsonView, ControllerBase.ATTR_VALIDATION_ERRORS, vepm.getMsg());
		}

		log.warn("Failed to process request", ex);
		res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return new ModelAndView(jsonView, ControllerBase.ATTR_EXCEPTION, msg);
	}

	private boolean isAnonymous() {
		if (securityContextResolver == null) {
			return true;
		}
		try {
			securityContextResolver.getUserUuid();
			return false;
		} catch (Throwable t) {
			// that's right, anonymous user cannot have user id
			return true;
		}
	}

	protected String getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			return auth.getName();
		}

		return SecurityMessageCodes.ANONYMOUS;
	}

	protected void respondWithJson(Object dto, HttpServletResponse response) {
		try {
			String json = getGson().toJson(dto);
			byte[] content = json.getBytes("UTF-8");
			response.setContentLength(content.length);
			response.setContentType("application/json;charset=UTF-8");
			ServletOutputStream outputStream = response.getOutputStream();
			outputStream.write(content);
			outputStream.flush();
		} catch (Exception exc) {
			throw new RuntimeException("Failed to write response body", exc);
		}
	}

	public Gson getGson() {
		return gson;
	}

	@Autowired(required = false)
	public void setGson(Gson gson) {
		this.gson = gson;
	}

	public SecurityContextResolver<?> getSecurityContextResolver() {
		return securityContextResolver;
	}

	@Autowired(required = false)
	public void setSecurityContextResolver(SecurityContextResolver<?> securityContextResolver) {
		this.securityContextResolver = securityContextResolver;
	}

	public MappingJackson2JsonView getJsonView() {
		return jsonView;
	}

	@Autowired(required = false)
	public void setJsonView(MappingJackson2JsonView jsonView) {
		this.jsonView = jsonView;
	}

	public ExceptionTranslator getExceptionTranslator() {
		return exceptionTranslator;
	}

	@Autowired(required = false)
	public void setExceptionTranslator(ExceptionTranslator exceptionTranslator) {
		this.exceptionTranslator = exceptionTranslator;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
