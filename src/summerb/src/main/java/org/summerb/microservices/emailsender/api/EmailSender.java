package org.summerb.microservices.emailsender.api;

import org.summerb.microservices.emailsender.api.dto.EmailMessage;

public interface EmailSender {
	void sendEmail(EmailMessage emailMessage);
}
