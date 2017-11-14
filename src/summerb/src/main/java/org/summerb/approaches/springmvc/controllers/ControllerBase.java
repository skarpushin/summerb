package org.summerb.approaches.springmvc.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.summerb.approaches.security.api.SecurityContextResolver;
import org.summerb.approaches.springmvc.model.ListPm;
import org.summerb.approaches.springmvc.model.PageMessage;
import org.summerb.approaches.springmvc.utils.ControllerExceptionHandlerStrategy;
import org.summerb.approaches.springmvc.utils.ControllerExceptionHandlerStrategyLegacyImpl;
import org.summerb.approaches.springmvc.utils.CurrentRequestUtils;
import org.summerb.microservices.users.api.dto.User;

/**
 * Base class for controllers, contains simple common operations
 * 
 * @author sergey.karpushin
 * 
 */
public abstract class ControllerBase implements ApplicationContextAware, InitializingBean {
	protected final Logger log = Logger.getLogger(getClass());

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

	@Override
	@SuppressWarnings("deprecation")
	public void afterPropertiesSet() throws Exception {
		if (exceptionHandlerStrategy == null) {
			// it's here for backwards compatibility, but expected to be injected in newer
			// projects
			ControllerExceptionHandlerStrategyLegacyImpl handler = new ControllerExceptionHandlerStrategyLegacyImpl(
					applicationContext);
			handler.setSecurityContextResolver(securityContextResolver);
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
}
