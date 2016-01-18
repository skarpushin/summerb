package org.summerb.approaches.springmvc.controllers;

import java.util.Map;

import javax.servlet.ServletOutputStream;
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
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.summerb.approaches.security.api.Roles;
import org.summerb.approaches.security.api.SecurityContextResolver;
import org.summerb.approaches.security.api.dto.NotAuthorizedResult;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.springmvc.Views;
import org.summerb.approaches.springmvc.model.ListPm;
import org.summerb.approaches.springmvc.model.MessageSeverity;
import org.summerb.approaches.springmvc.model.PageMessage;
import org.summerb.approaches.springmvc.model.ValidationErrorsPm;
import org.summerb.approaches.springmvc.utils.CurrentRequestUtils;
import org.summerb.approaches.springmvc.utils.ErrorUtils;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.utils.exceptions.ExceptionUtils;

import com.google.gson.Gson;

/**
 * Base class for controllers, contains simple common operations
 * 
 * @author sergey.karpushin
 * 
 */
public abstract class ControllerBase implements ApplicationContextAware, InitializingBean {
	private static final String X_TRANSLATE_AUTHORIZATION_ERRORS = "X-TranslateAuthorizationErrors";
	protected static final String ATTR_CURRENT_USER = "currentUser";
	protected static final String ATTR_VALIDATION_ERRORS = "ve";
	protected static final String ATTR_ERROR = "allErrorsMessages";
	protected static final String ATTR_EXCEPTION = "exc";
	protected static final String ATTR_EXCEPTION_STACKTRACE = "excst";
	protected static final String ATTR_RESULT = "result";

	public static final String ATTR_CONTENT_MESSAGES = "contentMessages";
	public static final String ATTR_PAGE_MESSAGES = "pageMessages";

	protected Logger log = Logger.getLogger(getClass());

	protected ApplicationContext applicationContext;

	@Autowired(required = false)
	private SecurityContextResolver<?> securityContextResolver;
	private MappingJackson2JsonView jsonView = new MappingJackson2JsonView();

	private Gson gson;

	@Override
	public void afterPropertiesSet() throws Exception {
		// nothing here, override in subclasses if needed
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
		addMessage(modelMap, pageMessage, ATTR_PAGE_MESSAGES);
	}

	protected void addContentMessage(Map<String, Object> modelMap, PageMessage pageMessage) {
		addMessage(modelMap, pageMessage, ATTR_CONTENT_MESSAGES);
	}

	@SuppressWarnings("unchecked")
	protected void addMessage(Map<String, Object> modelMap, PageMessage pageMessage, String messageBundleName) {
		ListPm<PageMessage> pageMessages = (ListPm<PageMessage>) modelMap.get(messageBundleName);
		if (pageMessages == null) {
			modelMap.put(messageBundleName, pageMessages = new ListPm<PageMessage>());
		}
		pageMessages.add(pageMessage);
	}

	@ExceptionHandler(Throwable.class)
	public ModelAndView handleUnexpectedControllerException(Throwable ex, HttpServletRequest req,
			HttpServletResponse res) {
		boolean isAcceptJson = req.getHeader("Accept") != null
				&& req.getHeader("Accept").startsWith("application/json");
		boolean isContentTypeJson = req.getContentType() != null && req.getContentType().startsWith("application/json");

		boolean isJsonOutputRequired = isAcceptJson || isContentTypeJson;

		return isJsonOutputRequired ? buildJsonError(ex, req, res) : buildHtmlError(ex);
	}

	private ModelAndView buildHtmlError(Throwable ex) {
		if (securityContextResolver != null
				&& (ex instanceof AccessDeniedException && !securityContextResolver.hasRole(Roles.ROLE_USER))) {
			throw new IllegalArgumentException("Exception will not be handled by default exception handler: " + ex);
		}
		log.error("Exception occured", ex);

		ModelAndView ret = new ModelAndView(Views.ERROR_UNEXPECTED_CLARIFIED);
		addPageMessage(ret.getModel(), new PageMessage(ErrorUtils.getAllMessages(ex), MessageSeverity.Danger));
		ret.getModel().put(ATTR_EXCEPTION, ex);
		ret.getModel().put(ATTR_EXCEPTION_STACKTRACE, ExceptionUtils.getThrowableStackTraceAsString(ex));
		return ret;
	}

	private ModelAndView buildJsonError(Throwable ex, HttpServletRequest req, HttpServletResponse res) {
		if (ex instanceof NotAuthorizedException) {
			NotAuthorizedResult naeResult = ((NotAuthorizedException) ex).getResult();
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
			if (Boolean.TRUE.equals(Boolean.valueOf(req.getHeader(X_TRANSLATE_AUTHORIZATION_ERRORS)))) {
				return new ModelAndView(jsonView, ATTR_EXCEPTION, ErrorUtils.getAllMessages(ex));
			} else {
				respondWithJson(naeResult, res);
				return null;
			}
		} else if (ex instanceof FieldValidationException) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			ValidationErrorsPm vepm = new ValidationErrorsPm(((FieldValidationException) ex).getErrors());
			return new ModelAndView(jsonView, ATTR_VALIDATION_ERRORS, vepm.getMsg());
		}

		log.warn("Failed to process request", ex);
		res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return new ModelAndView(jsonView, ATTR_EXCEPTION, ErrorUtils.getAllMessages(ex));
	}

	private void respondWithJson(Object dto, HttpServletResponse response) {
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

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public Gson getGson() {
		return gson;
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}

	public MappingJackson2JsonView getJsonView() {
		return jsonView;
	}

	public void setJsonView(MappingJackson2JsonView jsonView) {
		this.jsonView = jsonView;
	}
}
