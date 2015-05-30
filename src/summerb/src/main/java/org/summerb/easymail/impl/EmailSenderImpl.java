package org.summerb.easymail.impl;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.summerb.easymail.api.EmailMessageTemplate;
import org.summerb.easymail.api.EmailMessageTemplateResolver;
import org.summerb.easymail.api.EmailSender;
import org.summerb.easymail.api.EmailSenderTransport;
import org.summerb.easymail.api.dto.EmailMessage;
import org.summerb.easymail.api.dto.EmailRecipient;

public class EmailSenderImpl implements EmailSender {
	private EmailSenderTransport emailSenderTransport;
	private EmailRecipient to;
	private EmailMessageTemplateResolver emailMessageTemplateResolver;

	@Override
	public void send(String templateId, Locale locale, Object data) {
		try {
			EmailMessageTemplate emailMessageTemplate = emailMessageTemplateResolver.get(templateId, locale);
			EmailMessage emailMessage = emailMessageTemplate.applyTo(data);
			emailSenderTransport.send(emailMessage, to);
		} catch (Throwable t) {
			throw new RuntimeException("Failed to send email", t);
		}
	}

	@Autowired
	public void setEmailMessageTemplateResolver(EmailMessageTemplateResolver emailMessageTemplateResolver) {
		this.emailMessageTemplateResolver = emailMessageTemplateResolver;
	}

	@Autowired
	public void setEmailSenderTransport(EmailSenderTransport emailSenderTransport) {
		this.emailSenderTransport = emailSenderTransport;
	}

	@Required
	public void setTo(EmailRecipient to) {
		this.to = to;
	}

}
