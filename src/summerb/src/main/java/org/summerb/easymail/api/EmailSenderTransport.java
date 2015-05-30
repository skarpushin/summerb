package org.summerb.easymail.api;

import org.summerb.easymail.api.dto.EmailMessage;
import org.summerb.easymail.api.dto.EmailRecipient;

public interface EmailSenderTransport {
	void send(EmailMessage message, EmailRecipient to);
}
