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
package org.summerb.webappboilerplate.controllers;

import java.util.Map;
import java.util.concurrent.Callable;

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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.summerb.i18n.HasMessageCode;
import org.summerb.spring.security.api.SecurityContextResolver;
import org.summerb.users.api.dto.User;
import org.summerb.utils.exceptions.ExceptionUtils;
import org.summerb.utils.exceptions.translator.ExceptionTranslator;
import org.summerb.utils.exceptions.translator.ExceptionTranslatorLegacyImpl;
import org.summerb.webappboilerplate.model.ListPm;
import org.summerb.webappboilerplate.model.PageMessage;
import org.summerb.webappboilerplate.utils.ControllerExceptionHandlerStrategy;
import org.summerb.webappboilerplate.utils.ControllerExceptionHandlerStrategyLegacyImpl;
import org.summerb.webappboilerplate.utils.CurrentRequestUtils;

/**
 * Base class for controllers, contains simple common operations
 * 
 * @author sergey.karpushin
 * 
 */
public abstract class ControllerBase implements ApplicationContextAware, InitializingBean {
	protected final Logger log = LogManager.getLogger(getClass());

	public static final String ATTR_CURRENT_USER = "currentUser";
	public static final String ATTR_VALIDATION_ERRORS = "ve";
	public static final String ATTR_ERROR = "allErrorsMessages";
	public static final String ATTR_RESULT = "result";
	public static final String ATTR_EXCEPTION = "exc";
	public static final String ATTR_EXCEPTION_STACKTRACE = "excst";
	public static final String ATTR_CONTENT_MESSAGES = "contentMessages";
	public static final String ATTR_PAGE_MESSAGES = "pageMessages";

	protected ApplicationContext applicationContext;
	private SecurityContextResolver<? extends User> securityContextResolver;

	private ControllerExceptionHandlerStrategy exceptionHandlerStrategy;
	private ExceptionTranslator exceptionTranslator;

	@Override
	@SuppressWarnings("deprecation")
	public void afterPropertiesSet() throws Exception {
		if (exceptionTranslator == null) {
			exceptionTranslator = new ExceptionTranslatorLegacyImpl(applicationContext);
		}
		if (exceptionHandlerStrategy == null) {
			// it's here for backwards compatibility, but expected to be injected in newer
			// projects
			ControllerExceptionHandlerStrategyLegacyImpl handler = new ControllerExceptionHandlerStrategyLegacyImpl(
					applicationContext);
			handler.setSecurityContextResolver(securityContextResolver);
			handler.setApplicationContext(applicationContext);
			handler.setExceptionTranslator(exceptionTranslator);
			handler.afterPropertiesSet();
			exceptionHandlerStrategy = handler;
		}
	}

	/**
	 * Get message text by message code
	 */
	protected String msg(String code) {
		return msg(code, null);
	}

	/**
	 * Get message text by message code with optional arguments
	 */
	protected String msg(String code, Object[] optionalArgs) {
		return applicationContext.getMessage(code, optionalArgs, CurrentRequestUtils.getLocale());
	}

	public static void addPageMessage(Map<String, Object> modelMap, PageMessage pageMessage) {
		addMessage(modelMap, pageMessage, ATTR_PAGE_MESSAGES);
	}

	public static void addContentMessage(Map<String, Object> modelMap, PageMessage pageMessage) {
		addMessage(modelMap, pageMessage, ATTR_CONTENT_MESSAGES);
	}

	@SuppressWarnings("unchecked")
	public static void addMessage(Map<String, Object> modelMap, PageMessage pageMessage, String messageBundleName) {
		ListPm<PageMessage> pageMessages = (ListPm<PageMessage>) modelMap.get(messageBundleName);
		if (pageMessages == null) {
			modelMap.put(messageBundleName, pageMessages = new ListPm<PageMessage>());
		}
		pageMessages.add(pageMessage);
	}

	@ExceptionHandler(Throwable.class)
	public ModelAndView handleUnexpectedControllerException(Throwable ex, HttpServletRequest req,
			HttpServletResponse res) {
		return exceptionHandlerStrategy.handleUnexpectedControllerException(ex, req, res);
	}

	/**
	 * Alternative way to handle exceptions when handling requests. Instead of using
	 * generic error handler we might want to show the same page, but with error
	 * content; which is being set as model attribute {@link #ATTR_EXCEPTION}
	 */
	protected void mapExceptionsToModelAttr(Model model, Callable<?> callable) throws Exception {
		try {
			callable.call();
		} catch (Throwable t) {
			if (ExceptionUtils.findExceptionOfType(t, HasMessageCode.class) != null) {
				String userMessage = exceptionTranslator.buildUserMessage(t, LocaleContextHolder.getLocale());
				model.addAttribute(ATTR_EXCEPTION, userMessage);
			} else {
				throw t;
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ControllerExceptionHandlerStrategy getExceptionHandlerStrategy() {
		return exceptionHandlerStrategy;
	}

	@Autowired(required = false)
	public void setExceptionHandlerStrategy(ControllerExceptionHandlerStrategy exceptionHandlerStrategy) {
		this.exceptionHandlerStrategy = exceptionHandlerStrategy;
	}

	public SecurityContextResolver<? extends User> getSecurityContextResolver() {
		return securityContextResolver;
	}

	@Autowired(required = false)
	public void setSecurityContextResolver(SecurityContextResolver<? extends User> securityContextResolver) {
		this.securityContextResolver = securityContextResolver;
	}

	public ExceptionTranslator getExceptionTranslator() {
		return exceptionTranslator;
	}

	@Autowired(required = false)
	public void setExceptionTranslator(ExceptionTranslator exceptionTranslator) {
		this.exceptionTranslator = exceptionTranslator;
	}
}
