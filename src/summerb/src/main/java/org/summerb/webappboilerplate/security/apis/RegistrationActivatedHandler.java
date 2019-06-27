package org.summerb.webappboilerplate.security.apis;

import org.summerb.microservices.users.api.dto.User;
import org.summerb.utils.exceptions.GenericException;

public interface RegistrationActivatedHandler {

	void onRegistrationActivated(User user) throws GenericException;

}
