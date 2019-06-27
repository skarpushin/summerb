package org.summerb.webappboilerplate.security.apis;

import org.summerb.microservices.emailsender.api.EmailMessageBuilder;
import org.summerb.microservices.users.api.dto.User;

public interface SecurityMailsMessageBuilderFactory {
	EmailMessageBuilder createEmailMessageBuilderFromArticle(String articleKey, User sender);

	User getAccountOperationsSender();

	EmailMessageBuilder getRegistrationEmailBuilder();

	EmailMessageBuilder getPasswordResetEmailBuilder();

}
