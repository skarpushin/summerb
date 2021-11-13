/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.email.impl;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import org.summerb.email.api.EmailChannelProperties;

public class EmailChannelPropertiesGmailImpl implements EmailChannelProperties {
	private String smtpHost;
	private String smtpUser;
	private String smtpPassword;
	private Integer smtpPort;
	private String sslFactory;
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
			Properties props = System.getProperties();

			// props.put("mail.debug", "true");

			if (getSmtpHost() != null) {
				props.put("mail.smtp.host", getSmtpHost());
			}

			if (getSmtpUser() != null) {
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.socketFactory.port", getSmtpPort());
				props.put("mail.smtp.socketFactory.class", getSslFactory());
				// props.put("mail.smtp.starttls.enable", "true");
			}

			if (getSmtpPort() != null) {
				props.put("mail.smtp.port", getSmtpPort().intValue());
			}

			emailChannelProperties = props;
		}

		return emailChannelProperties;
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

	public String getSslFactory() {
		return sslFactory;
	}

	public void setSslFactory(String sslFactory) {
		this.sslFactory = sslFactory;
	}

}
