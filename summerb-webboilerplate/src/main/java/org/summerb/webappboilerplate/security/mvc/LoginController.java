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
package org.summerb.webappboilerplate.security.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.summerb.security.api.AuditEvents;
import org.summerb.security.api.dto.ScalarValue;
import org.summerb.security.impl.AuditEventsDefaultImpl;
import org.summerb.spring.security.SecurityMessageCodes;
import org.summerb.users.api.PermissionService;
import org.summerb.users.api.UserService;
import org.summerb.users.api.dto.User;
import org.summerb.users.api.exceptions.UserNotFoundException;
import org.summerb.utils.exceptions.ExceptionUtils;
import org.summerb.utils.exceptions.GenericException;
import org.summerb.utils.exceptions.translator.ExceptionTranslatorSimplified;
import org.summerb.validation.FieldValidationException;
import org.summerb.validation.ValidationError;
import org.summerb.webappboilerplate.controllers.ControllerBase;
import org.summerb.webappboilerplate.model.ValidationErrorsVm;
import org.summerb.webappboilerplate.security.apis.SecurityActionsUrlsProvider;
import org.summerb.webappboilerplate.security.apis.SecurityViewNamesProvider;
import org.summerb.webappboilerplate.security.apis.UsersServiceFacade;
import org.summerb.webappboilerplate.security.dto.PasswordChange;
import org.summerb.webappboilerplate.security.dto.PasswordReset;
import org.summerb.webappboilerplate.security.dto.Registration;
import org.summerb.webappboilerplate.security.impls.UserAccountChangeHadlersDefaultImpl;
import org.summerb.webappboilerplate.utils.AbsoluteUrlBuilder;
import org.summerb.webappboilerplate.utils.CaptchaController;

/**
 * This controller provides request/response-based actions for common
 * account-related operations
 * 
 * @author sergeyk
 *
 */
@Controller
public class LoginController extends ControllerBase {
	public static final String AUDIT_PASSWORD_RESET_TOKEN_INVALID = "PWDRSTINV";

	private Logger log = LogManager.getLogger(getClass());

	private static final String ATTR_PASSWORD_RESET_TOKEN = "passwordResetToken";
	private static final String ATTR_ACTIVATED = "activated";
	private static final String ATTR_REGISTERED = "registered";
	private static final String ATTR_REGISTRATION = "registration";
	private static final String ATTR_PASSWORD_RESET_REQUEST = "resetPasswordRequest";
	private static final String ATTR_PASSWORD_RESET = "passwordReset";
	private static final String ATTR_RESET_OK = "resetOk";
	private static final String ATTR_PASSWORD_CHANGE = "passwordChange";
	private static final String ATTR_PASSWORD_CHANGED = "passwordChanged";
	private static final String ATTR_FORM_ACCEPTED = "formAccepted";

	private UsersServiceFacade usersServiceFacade;
	private UserService userService;
	private PermissionService permissionService;
	private AbsoluteUrlBuilder absoluteUrlBuilder;
	private RedirectStrategy redirectStrategy;
	private SecurityViewNamesProvider views;
	private SecurityActionsUrlsProvider securityActionsUrlsProvider;
	private ExceptionTranslatorSimplified exceptionTranslatorSimplified;
	private AuditEvents auditEvents;

	@Autowired(required = false)
	@Value("#{ props.properties['profile.dev'] }")
	protected boolean isDevMode;

	@Autowired(required = false)
	@Value("#{ props.properties['profile.autotest'] }")
	protected boolean isAutoTestMode;

	public LoginController() {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (views == null) {
			views = new SecurityViewNamesProviderDefaultImpl();
		}

		if (securityActionsUrlsProvider == null) {
			securityActionsUrlsProvider = new SecurityActionsUrlsProviderDefaultImpl();
		}

		if (redirectStrategy == null) {
			redirectStrategy = new DefaultRedirectStrategy();
		}

		if (auditEvents == null) {
			auditEvents = new AuditEventsDefaultImpl();
		}

		super.afterPropertiesSet();
	}

	@RequestMapping(method = RequestMethod.GET, value = SecurityActionsUrlsProviderDefaultImpl.LOGIN_FORM)
	public String getLoginForm(Model model) {
		return views.loginForm();
	}

	@RequestMapping(method = RequestMethod.GET, value = SecurityActionsUrlsProviderDefaultImpl.LOGIN_FAILED)
	public String handleLoginFailed(Model model, HttpServletRequest request) {
		Exception lastException = (Exception) request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		if (lastException != null) {
			log.info("Login failed due to exception", lastException);
			model.addAttribute("lastExceptionMessage", exceptionTranslatorSimplified.buildUserMessage(lastException));
			// Delete it from session to avoid excessive memory consumption
			request.getSession().removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}

		model.addAttribute("loginError", true);

		// Add validation errors
		FieldValidationException validationErrors = ExceptionUtils.findExceptionOfType(lastException,
				FieldValidationException.class);
		if (validationErrors != null) {
			for (ValidationError error : validationErrors.getErrors()) {
				model.addAttribute("ve_" + error.getFieldToken(), msg(error.getMessageCode(), error.getMessageArgs()));
			}
		}

		// add login failed message
		return getLoginForm(model);
	}

	@RequestMapping(method = RequestMethod.GET, value = SecurityActionsUrlsProviderDefaultImpl.INVALID_SESSION)
	public ModelAndView handleInvalidSession(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("requestedUrl") String requestedUrl) throws Exception {
		redirectStrategy.sendRedirect(request, response, requestedUrl);
		return null;
	}

	@RequestMapping(method = RequestMethod.GET, value = SecurityActionsUrlsProviderDefaultImpl.REGISTER)
	public String getRegisterForm(Model model, HttpServletRequest request) {
		model.addAttribute(ATTR_REGISTRATION, new Registration());
		CaptchaController.putToken("register", request);
		return views.registerForm();
	}

	@RequestMapping(method = RequestMethod.POST, value = SecurityActionsUrlsProviderDefaultImpl.REGISTER)
	public String processRegisterForm(@ModelAttribute(ATTR_REGISTRATION) Registration registration, Model model,
			HttpServletRequest request) {
		if (!isAutoTestMode) {
			CaptchaController.assertCaptchaTokenValid("register", registration.getCaptcha(), request);
		}

		try {
			// Create user
			User user = usersServiceFacade.registerUser(registration);
			model.addAttribute(ATTR_REGISTERED, true);

			if (isDevMode) {
				String activationAbsoluteLink = absoluteUrlBuilder
						.buildExternalUrl(securityActionsUrlsProvider.buildRegistrationActivationPath(user, null));
				model.addAttribute(UserAccountChangeHadlersDefaultImpl.ATTR_ACTIVATION_LINK, activationAbsoluteLink);
			}
		} catch (FieldValidationException fve) {
			model.addAttribute(ControllerBase.ATTR_VALIDATION_ERRORS, new ValidationErrorsVm(fve.getErrors()));
		}
		return views.registerForm();
	}

	@RequestMapping(method = RequestMethod.GET, value = SecurityActionsUrlsProviderDefaultImpl.ACTIVATE)
	public String getRegistrationActivationForm(Model model, HttpServletRequest request,
			@RequestParam(value = SecurityActionsUrlsProviderDefaultImpl.PARAM_ACTIVATION_UUID) String activationUuid)
			throws GenericException {
		// Create user
		usersServiceFacade.activateRegistration(activationUuid);
		model.addAttribute(ATTR_ACTIVATED, true);

		return views.activateRegistration();
	}

	@RequestMapping(method = RequestMethod.GET, value = SecurityActionsUrlsProviderDefaultImpl.REQUEST_RESET)
	public String getPasswordResetRequestForm(Model model, HttpServletRequest request) {
		model.addAttribute(ATTR_PASSWORD_RESET_REQUEST, new Registration());
		CaptchaController.putToken("request-reset", request);
		return views.resetPasswordRequest();
	}

	@RequestMapping(method = RequestMethod.POST, value = SecurityActionsUrlsProviderDefaultImpl.REQUEST_RESET)
	public String processPasswordResetRequestForm(
			@ModelAttribute(ATTR_PASSWORD_RESET_REQUEST) Registration registration, Model model,
			HttpServletRequest request) {
		if (!isAutoTestMode) {
			CaptchaController.assertCaptchaTokenValid("request-reset", registration.getCaptcha(), request);
		}

		try {
			String passwordResetToken = usersServiceFacade.getNewPasswordResetToken(registration.getEmail());

			// Generate registration link
			String passwordResetAbsoluteLink = absoluteUrlBuilder.buildExternalUrl(
					securityActionsUrlsProvider.buildPasswordResetPath(registration.getEmail(), passwordResetToken));

			model.addAttribute(ATTR_FORM_ACCEPTED, true);
			if (isDevMode) {
				model.addAttribute(UserAccountChangeHadlersDefaultImpl.ATTR_PASSWORD_RESET_LINK,
						passwordResetAbsoluteLink);
			}
		} catch (FieldValidationException fve) {
			model.addAttribute(ControllerBase.ATTR_VALIDATION_ERRORS, new ValidationErrorsVm(fve.getErrors()));
		}

		return views.resetPasswordRequest();
	}

	@RequestMapping(method = RequestMethod.GET, value = SecurityActionsUrlsProviderDefaultImpl.RESET_PASSWORD)
	public String getPasswordResetForm(@PathVariable(ATTR_PASSWORD_RESET_TOKEN) String passwordResetToken,
			@RequestParam(User.FN_EMAIL) String email, Model model, HttpServletRequest request)
			throws UserNotFoundException, FieldValidationException, GenericException {

		// Check if token valid
		if (!usersServiceFacade.isPasswordResetTokenValid(email, passwordResetToken)) {
			auditEvents.report(AUDIT_PASSWORD_RESET_TOKEN_INVALID, ScalarValue.forV(passwordResetToken));
			throw new GenericException(SecurityMessageCodes.INVALID_PASSWORD_RESET_TOKEN);
		}

		// Now let's show password reset form
		model.addAttribute(ATTR_PASSWORD_RESET, new PasswordReset());
		model.addAttribute(User.FN_EMAIL, email);
		model.addAttribute(ATTR_PASSWORD_RESET_TOKEN, passwordResetToken);

		return views.resetPassword();
	}

	@RequestMapping(method = RequestMethod.POST, value = SecurityActionsUrlsProviderDefaultImpl.RESET_PASSWORD)
	public String processPasswordResetForm(
			@ModelAttribute(ATTR_PASSWORD_RESET_REQUEST) PasswordReset resetPasswordRequest,
			@PathVariable(ATTR_PASSWORD_RESET_TOKEN) String passwordResetToken,
			@RequestParam(User.FN_EMAIL) String email, Model model, HttpServletRequest request)
			throws UserNotFoundException {

		model.addAttribute(User.FN_EMAIL, email);
		model.addAttribute(ATTR_PASSWORD_RESET_TOKEN, passwordResetToken);
		model.addAttribute(ATTR_PASSWORD_RESET, resetPasswordRequest);

		try {
			usersServiceFacade.resetPassword(email, passwordResetToken, resetPasswordRequest);
			model.addAttribute(ATTR_RESET_OK, true);
		} catch (FieldValidationException fve) {
			model.addAttribute(ControllerBase.ATTR_VALIDATION_ERRORS, new ValidationErrorsVm(fve.getErrors()));
		}

		return views.resetPassword();
	}

	@Secured({ "ROLE_USER" })
	@RequestMapping(method = RequestMethod.GET, value = SecurityActionsUrlsProviderDefaultImpl.CHANGE_PASSWORD)
	public String getPasswordChangeForm(Model model, HttpServletRequest request) {
		model.addAttribute(ATTR_PASSWORD_CHANGE, new PasswordChange());
		return views.changePassword();
	}

	@Secured({ "ROLE_USER" })
	@RequestMapping(method = RequestMethod.POST, value = SecurityActionsUrlsProviderDefaultImpl.CHANGE_PASSWORD)
	public String processPasswordChangeForm(@ModelAttribute(ATTR_PASSWORD_CHANGE) PasswordChange passwordChange,
			Model model, HttpServletRequest request) throws UserNotFoundException {

		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usersServiceFacade.changePassword(auth.getName(), passwordChange);
			model.addAttribute(ATTR_PASSWORD_CHANGED, true);
		} catch (FieldValidationException fve) {
			model.addAttribute(ControllerBase.ATTR_VALIDATION_ERRORS, new ValidationErrorsVm(fve.getErrors()));
		}
		return views.changePassword();
	}

	public RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}

	@Autowired(required = false)
	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

	public SecurityViewNamesProvider getViews() {
		return views;
	}

	@Autowired(required = false)
	public void setViews(SecurityViewNamesProvider views) {
		this.views = views;
	}

	public SecurityActionsUrlsProvider getSecurityActionsUrlsProvider() {
		return securityActionsUrlsProvider;
	}

	@Autowired(required = false)
	public void setSecurityActionsUrlsProvider(SecurityActionsUrlsProvider securityActionsUrlsProvider) {
		this.securityActionsUrlsProvider = securityActionsUrlsProvider;
	}

	public UsersServiceFacade getUsersServiceFacade() {
		return usersServiceFacade;
	}

	@Autowired
	public void setUsersServiceFacade(UsersServiceFacade usersServiceFacade) {
		this.usersServiceFacade = usersServiceFacade;
	}

	public UserService getUserService() {
		return userService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	@Autowired
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public AbsoluteUrlBuilder getAbsoluteUrlBuilder() {
		return absoluteUrlBuilder;
	}

	@Autowired
	public void setAbsoluteUrlBuilder(AbsoluteUrlBuilder absoluteUrlBuilder) {
		this.absoluteUrlBuilder = absoluteUrlBuilder;
	}

	public ExceptionTranslatorSimplified getExceptionTranslatorSimplified() {
		return exceptionTranslatorSimplified;
	}

	@Autowired
	public void setExceptionTranslatorSimplified(ExceptionTranslatorSimplified exceptionTranslatorSimplified) {
		this.exceptionTranslatorSimplified = exceptionTranslatorSimplified;
	}

	public AuditEvents getAuditLog() {
		return auditEvents;
	}

	@Autowired(required = false)
	public void setAuditLog(AuditEvents auditEvents) {
		this.auditEvents = auditEvents;
	}

}
