package org.summerb.webappboilerplate.security.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.summerb.approaches.security.api.CurrentUserNotFoundException;
import org.summerb.approaches.spring.security.api.SecurityContextResolver;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.microservices.users.api.UserService;
import org.summerb.microservices.users.api.dto.User;
import org.summerb.microservices.users.api.exceptions.UserNotFoundException;
import org.summerb.webappboilerplate.controllers.ControllerBase;
import org.summerb.webappboilerplate.security.apis.UsersServiceFacade;
import org.summerb.webappboilerplate.security.dto.PasswordChange;
import org.summerb.webappboilerplate.security.dto.Registration;

/**
 * This controller provides request/response-based actions for common
 * account-related operations
 * 
 * @author sergeyk
 *
 */
@RestController
@RequestMapping("/rest/login")
public class LoginRestController extends ControllerBase {
	@Autowired
	private UsersServiceFacade usersServiceFacade;
	@Autowired
	private UserService userService;
	@Autowired
	private SecurityContextResolver<User> securityContextResolver;

	public LoginRestController() {
	}

	@RequestMapping(method = RequestMethod.POST, value = "register")
	public User register(@RequestBody Registration registration) throws FieldValidationException {
		return usersServiceFacade.registerUser(registration);
	}

	@Secured({ "ROLE_USER" })
	@RequestMapping(method = RequestMethod.POST, value = "change")
	public User processPasswordChangeForm(@RequestBody PasswordChange passwordChange)
			throws UserNotFoundException, FieldValidationException {
		User user = securityContextResolver.getUser();
		usersServiceFacade.changePassword(user.getEmail(), passwordChange);
		return user;
	}

	@Secured({ "ROLE_USER" })
	@RequestMapping(method = RequestMethod.GET, value = "user")
	public User getUser() throws UserNotFoundException, CurrentUserNotFoundException {
		return userService.getUserByUuid(securityContextResolver.getUser().getUuid());
	}

	@Secured({ "ROLE_USER" })
	@RequestMapping(method = RequestMethod.POST, value = "user")
	public User updateUser(@RequestBody User user)
			throws UserNotFoundException, CurrentUserNotFoundException, FieldValidationException {
		userService.updateUser(user);
		return user;
	}

}
