package org.summerb.webappboilerplate.security.apis;

import org.summerb.microservices.users.api.dto.User;

public interface UserRegisteredHandler {

	/**
	 * Method called after user account is created in the system. Next usrs's
	 * step is to activate this account.
	 */
	void onUserRegistered(User user);

}
