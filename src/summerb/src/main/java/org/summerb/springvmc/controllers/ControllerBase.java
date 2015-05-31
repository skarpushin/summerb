package org.summerb.springvmc.controllers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.summerb.i18n.I18nUtils;
import org.summerb.security.api.Roles;
import org.summerb.security.api.SecurityContextResolver;
import org.summerb.security.api.exceptions.CurrentUserNotFoundException;
import org.summerb.springvmc.Views;
import org.summerb.springvmc.controllers.model.MessageSeverity;
import org.summerb.springvmc.controllers.model.PageMessage;
import org.summerb.springvmc.utils.CurrentRequestUtils;
import org.summerb.springvmc.utils.ListVm;

/**
 * Base class for controllers, contains simple common operations
 * 
 * @author sergey.karpushin
 * 
 */
public abstract class ControllerBase implements ApplicationContextAware, InitializingBean {
	protected static final String ATTR_CURRENT_USER = "currentUser";
	protected static final String ATTR_VALIDATION_ERRORS = "ve";
	protected static final String ATTR_EXCEPTION = "exc";
	protected static final String ATTR_EXCEPTION_STACKTRACE = "excst";
	public static final String ATTR_CONTENT_MESSAGES = "contentMessages";
	public static final String ATTR_PAGE_MESSAGES = "pageMessages";

	protected Logger log = Logger.getLogger(getClass());

	protected ApplicationContext applicationContext;

	@Autowired
	protected SecurityContextResolver securityContextResolver;


	@Override
	public void afterPropertiesSet() throws Exception {
		// nothing here, override in subclasses if needed
	}

	protected String getCurrentUserUuid() {
		return securityContextResolver.getUser().getUsername();
	}

	protected String findCurrentUserUuid() {
		try {
			return securityContextResolver.getUser().getUsername();
		} catch (CurrentUserNotFoundException e) {
			return null;
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

	protected void addPageMessage(Map<String, Object> modelMap, PageMessage pageMessage) {
		addMessage(modelMap, pageMessage, ControllerBase.ATTR_PAGE_MESSAGES);
	}

	protected void addContentMessage(Map<String, Object> modelMap, PageMessage pageMessage) {
		addMessage(modelMap, pageMessage, ControllerBase.ATTR_CONTENT_MESSAGES);
	}

	@SuppressWarnings("unchecked")
	protected void addMessage(Map<String, Object> modelMap, PageMessage pageMessage, String messageBundleName) {
		ListVm<PageMessage> pageMessages = (ListVm<PageMessage>) modelMap.get(messageBundleName);
		if (pageMessages == null) {
			modelMap.put(messageBundleName, pageMessages = new ListVm<PageMessage>());
		}
		pageMessages.add(pageMessage);
	}

	protected String getThrowableStackTraceAsString(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString(); // stack trace as a string
	}

	@ExceptionHandler(Throwable.class)
	public ModelAndView handleUnexpectedControllerException(Throwable ex, HttpServletRequest req,
			HttpServletResponse res) {
		if (ex instanceof AccessDeniedException && !securityContextResolver.hasRole(Roles.ROLE_USER)) {
			throw new IllegalArgumentException("Exception will not be handled by default exception handler: " + ex);
		}
		log.error("Exception occured", ex);

		ModelAndView ret = new ModelAndView(Views.ERROR_UNEXPECTED_CLARIFIED);
		addPageMessage(ret.getModel(), new PageMessage(I18nUtils.buildMessagesChain(ex, applicationContext),
				MessageSeverity.Danger));
		ret.getModel().put(ATTR_EXCEPTION, ex);
		ret.getModel().put(ATTR_EXCEPTION_STACKTRACE, getThrowableStackTraceAsString(ex));
		return ret;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
