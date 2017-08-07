package org.summerb.approaches.springmvc.security.implsrest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.summerb.approaches.springmvc.security.apis.JsonResponseWriter;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.approaches.validation.ValidationErrors;
import org.summerb.utils.exceptions.ExceptionUtils;
import org.summerb.utils.exceptions.dto.ExceptionInfo;
import org.summerb.utils.exceptions.dto.GenericServerErrorResult;
import org.summerb.utils.exceptions.translator.ExceptionTranslator;

public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler, ApplicationContextAware {
	private JsonResponseWriter jsonResponseHelper;
	private ExceptionTranslator exceptionTranslator;
	private ApplicationContext applicationContext;

	public RestAuthenticationFailureHandler() {
		jsonResponseHelper = new JsonResponseHelperGsonImpl();
	}

	public RestAuthenticationFailureHandler(JsonResponseWriter jsonResponseHelper) {
		this.jsonResponseHelper = jsonResponseHelper;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		FieldValidationException fve = ExceptionUtils.findExceptionOfType(exception, FieldValidationException.class);
		if (fve != null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			jsonResponseHelper.writeResponseBody(new ValidationErrors(fve.getErrors()), response);
			return;
		}

		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		GenericServerErrorResult responseBody = new GenericServerErrorResult(
				exceptionTranslator.buildUserMessage(exception, applicationContext, LocaleContextHolder.getLocale()),
				new ExceptionInfo(exception));
		jsonResponseHelper.writeResponseBody(responseBody, response);
	}

	public ExceptionTranslator getExceptionTranslator() {
		return exceptionTranslator;
	}

	@Autowired
	public void setExceptionTranslator(ExceptionTranslator exceptionTranslator) {
		this.exceptionTranslator = exceptionTranslator;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
