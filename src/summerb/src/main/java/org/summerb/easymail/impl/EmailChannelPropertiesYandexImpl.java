package org.summerb.easymail.impl;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import org.summerb.easymail.api.EmailChannelProperties;

public class EmailChannelPropertiesYandexImpl implements EmailChannelProperties {
	private String smtpHost;
	private String smtpUser;
	private String smtpPassword;
	private Integer smtpPort;
	private Properties emailChannelProperties;

	private Authenticator authenticator = new Authenticator() {
		PasswordAuthentication passwordAuthentication;

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			if (passwordAuthentication == null) {
				passwordAuthentication = new PasswordAuthentication(getSmtpUser(), getSmtpPassword());
			}
			return passwordAuthentication;
		}
	};

	@Override
	public Properties getProperties() {
		if (emailChannelProperties == null) {
			emailChannelProperties = buildProperties();
		}
		return emailChannelProperties;
	}

	private Properties buildProperties() {
		try {
			Properties props = System.getProperties();

			if (getSmtpHost() != null) {
				props.put("mail.smtp.host", getSmtpHost());
			}

			if (getSmtpUser() != null) {
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
			}

			if (getSmtpPort() != null) {
				props.put("mail.smtp.port", getSmtpPort().intValue());
			}

			return props;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to build SMTP properties", t);
		}
	}

	@Override
	public Authenticator getAuthenticator() {
		return authenticator;
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public String getSmtpUser() {
		return smtpUser;
	}

	public void setSmtpUser(String smtpUser) {
		this.smtpUser = smtpUser;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPapssword) {
		this.smtpPassword = smtpPapssword;
	}

	public Integer getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(Integer smtpPort) {
		this.smtpPort = smtpPort;
	}

}
