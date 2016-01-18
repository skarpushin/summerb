package org.summerb.microservices.emailsender.impl;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.summerb.microservices.emailsender.api.EmailChannelProperties;
import org.summerb.microservices.emailsender.api.EmailSender;
import org.summerb.microservices.emailsender.api.dto.EmailMessage;

import com.google.common.base.Preconditions;

/**
 * use this to send email
 * 
 * see: http://www.sql.ru/forum/actualthread.aspx?tid=244089 see:
 * http://ru.wikipedia.org/wiki/JavaMail
 */
public class EmailSenderImpl implements EmailSender {
	private Logger log = Logger.getLogger(getClass());

	private EmailChannelProperties emailChannelProperties;

	private Session emailSession;

	@Override
	public void sendEmail(EmailMessage emailMessage) {
		Preconditions.checkArgument(emailMessage != null);

		try {
			MimeMessage message = new MimeMessage(getEmailSession());
			message.setHeader("Content-Type", "text/plain;charset=utf-8");
			message.setHeader("Content-Transfer-Encoding", "base64");
			message.setFrom(new InternetAddress(emailMessage.getFromAddress(), emailMessage.getFromName()));
			message.addRecipient(Message.RecipientType.TO,
					new InternetAddress(emailMessage.getToAddress(), emailMessage.getToName()));
			message.setSubject(emailMessage.getSubject(), "UTF-8");
			message.setText(emailMessage.getBody(), "UTF-8");

			Transport.send(message);
		} catch (Exception e) {
			log.error("Can't send email message", e);
			throw new RuntimeException("Can't send email message", e);
		}
	}

	protected Session getEmailSession() {
		if (emailSession == null) {
			Preconditions.checkState(getEmailChannelProperties() != null);

			emailSession = Session.getDefaultInstance(getEmailChannelProperties().getProperties(),
					getEmailChannelProperties().getAuthenticator());
		}
		return emailSession;
	}

	public EmailChannelProperties getEmailChannelProperties() {
		return emailChannelProperties;
	}

	@Required
	public void setEmailChannelProperties(EmailChannelProperties emailChannelProperties) {
		this.emailChannelProperties = emailChannelProperties;
	}

}
