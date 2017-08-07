package org.summerb.approaches.springmvc.security.apis;

import org.summerb.microservices.users.api.dto.User;

public interface PasswordResetArmedHandler {

	void onPasswordResetRequested(User user, String passwordResetToken);

}
