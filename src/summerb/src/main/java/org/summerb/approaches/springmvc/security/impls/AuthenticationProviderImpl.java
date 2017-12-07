package org.summerb.approaches.springmvc.security.impls;

import java.util.List;

import org.apache.log4j.Logger;
import org.owasp.encoder.Encode;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.summerb.approaches.security.api.AuditLog;
import org.summerb.approaches.security.impl.AuditLogDefaultImpl;
import org.summerb.approaches.springmvc.security.SecurityConstants;
import org.summerb.approaches.springmvc.security.SecurityMessageCodes;
import org.summerb.approaches.springmvc.security.apis.LoginEligibilityVerifier;
import org.summerb.approaches.springmvc.security.dto.UserDetailsImpl;
import org.summerb.approaches.springmvc.security.ve.PasswordInvalidValidationError;
import org.summerb.approaches.springmvc.security.ve.UserNotFoundValidationError;
import org.summerb.approaches.springmvc.utils.CurrentRequestUtils;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.microservices.users.api.PasswordService;
import org.summerb.microservices.users.api.PermissionService;
import org.summerb.microservices.users.api.UserService;
import org.summerb.microservices.users.api.dto.User;
import org.summerb.microservices.users.api.exceptions.InvalidPasswordException;
import org.summerb.microservices.users.api.exceptions.UserNotFoundException;

/**
 * Our own impl that uses {@link UserService} and {@link PermissionService}
 * 
 * @author sergeyk
 *
 */
public class AuthenticationProviderImpl implements AuthenticationProvider, InitializingBean, ApplicationContextAware {
	public static final String AUDIT_INVALID_PASSWORD = "INVPWD";
	public static final String AUDIT_USER_NOT_FOUND = "USRNFE";
	public static final String AUDIT_INVALID_LOGIN = "INVUN";
	public static final String AUDIT_LOGIN_OK = "LGNOK";

	protected final Logger logger = Logger.getLogger(getClass());

	private PasswordEncoder passwordEncoder;
	private PasswordService passwordService;
	private ApplicationContext applicationContext;
	private PermissionService permissionService;
	private UserService userService;
	private LoginEligibilityVerifier loginEligibilityVerifier;
	private AuditLog auditLog = new AuditLogDefaultImpl();

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// Ensure that all conditions apply
		Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication,
				getMessage("AbstractUserDetailsAuthenticationProvider.onlySupports",
						"Only UsernamePasswordAuthenticationToken is supported"));
		// check we have credentials specified
		if (authentication.getCredentials() == null) {
			logger.debug("Authentication failed: no credentials provided");
			throw new BadCredentialsException(
					getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}

		// Determine user-name
		String username = (authentication.getPrincipal() == null) ? "" : authentication.getName();
		// Encode password
		String presentedPlainPassword = authentication.getCredentials().toString();

		try {
			if (loginEligibilityVerifier != null) {
				loginEligibilityVerifier.validateUserAllowedToLogin(username);
			}

			// Proceed with authentication
			// get user
			User user = userService.getUserByEmail(username);
			// check password
			if (!passwordService.isUserPasswordValid(user.getUuid(), presentedPlainPassword)) {
				throw new InvalidPasswordException();
			}
			// get user permission
			List<String> permissions = permissionService.findUserPermissionsForSubject(SecurityConstants.DOMAIN,
					user.getUuid(), null);

			UserDetailsImpl userDetails = new UserDetailsImpl(user, "[PASSWORD REMOVED]", permissions, null);

			UsernamePasswordAuthenticationToken ret = new UsernamePasswordAuthenticationToken(userDetails,
					authentication.getCredentials(), userDetails.getAuthorities());
			ret.setDetails(authentication.getDetails());
			auditLog.report(AUDIT_LOGIN_OK, toJsString(user.getEmail()));
			return ret;
		} catch (FieldValidationException e) {
			auditLog.report(AUDIT_INVALID_LOGIN, toJsString(username));
			throw buildBadCredentialsExc(e);
		} catch (UserNotFoundException e) {
			auditLog.report(AUDIT_USER_NOT_FOUND, toJsString(username));
			throw buildBadCredentialsExc(new FieldValidationException(new UserNotFoundValidationError()));
		} catch (InvalidPasswordException e) {
			auditLog.report(AUDIT_INVALID_PASSWORD, toJsString(username));
			throw buildBadCredentialsExc(new FieldValidationException(new PasswordInvalidValidationError()));
		} catch (Throwable t) {
			throw new AuthenticationServiceException(
					getMessage(SecurityMessageCodes.AUTH_FATAL, "Fatal authentication exception"), t);
		}
	}

	private String toJsString(String email) {
		return "\"" + Encode.forJavaScript(email) + "\"";
	}

	protected BadCredentialsException buildBadCredentialsExc(FieldValidationException e) {
		return new BadCredentialsException(getMessage(SecurityMessageCodes.BAD_CREDENTIALS, "Bad credentials"), e);
	}

	private String getMessage(String msgCode, String defaultValue) {
		return applicationContext.getMessage(msgCode, null, defaultValue, CurrentRequestUtils.getLocale());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

	@Autowired
	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	@Autowired
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public UserService getUserService() {
		return userService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public PasswordService getPasswordService() {
		return passwordService;
	}

	@Autowired
	public void setPasswordService(PasswordService passwordService) {
		this.passwordService = passwordService;
	}

	public LoginEligibilityVerifier getLoginEligibilityVerifier() {
		return loginEligibilityVerifier;
	}

	@Autowired(required = false)
	public void setLoginEligibilityVerifier(LoginEligibilityVerifier loginEligibilityVerifier) {
		this.loginEligibilityVerifier = loginEligibilityVerifier;
	}

	public AuditLog getAuditLog() {
		return auditLog;
	}

	@Autowired(required = false)
	public void setAuditLog(AuditLog auditLog) {
		this.auditLog = auditLog;
	}

}
