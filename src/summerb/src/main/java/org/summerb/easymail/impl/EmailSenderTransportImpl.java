package org.summerb.easymail.impl;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.easymail.api.EmailChannelProperties;
import org.summerb.easymail.api.EmailSenderTransport;
import org.summerb.easymail.api.dto.EmailMessage;
import org.summerb.easymail.api.dto.EmailRecipient;

import com.google.common.base.Preconditions;

public class EmailSenderTransportImpl implements EmailSenderTransport {
	private Logger log = Logger.getLogger(getClass());
	private EmailRecipient from;
	private EmailChannelProperties emailChannelProperties;
	private Session emailSession;

	public void setFrom(EmailRecipient from) {
		this.from = from;
	}

	@Autowired
	public void setEmailChannelProperties(EmailChannelProperties emailChannelProperties) {
		this.emailChannelProperties = emailChannelProperties;
	}

	@Override
	public void send(EmailMessage emailMessage, EmailRecipient to) {
		try {
			Preconditions.checkArgument(emailMessage != null);

			MimeMessage message = new MimeMessage(getEmailSession());
			message.setHeader("Content-Type", "text/plain;charset=utf-8");
			message.setHeader("Content-Transfer-Encoding", "base64");
			message.setFrom(new InternetAddress(from.getEmail(), from.getName()));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to.getEmail(), to.getName()));
			message.setSubject(emailMessage.getSubject(), "UTF-8");
			message.setText(emailMessage.getBody(), "UTF-8");

			Transport.send(message);
			log.trace("Sent email: " + emailMessage.getSubject() + " " + emailMessage.getBody());
		} catch (Throwable t) {
			throw new RuntimeException("Failed to send email", t);
		}
	}

	protected Session getEmailSession() {
		if (emailSession == null) {
			Preconditions.checkState(emailChannelProperties != null);

			emailSession = Session.getDefaultInstance(emailChannelProperties.getProperties(),
					emailChannelProperties.getAuthenticator());
		}
		return emailSession;
	}

}
