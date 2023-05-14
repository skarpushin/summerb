/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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

import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Properties;

import javax.mail.Authenticator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.summerb.email.api.EmailChannelProperties;
import org.summerb.email.api.dto.EmailMessage;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;

public class EmailSenderImplTest {
	private static final int SMTP_PORT = 1081;

	private SimpleSmtpServer server;

	@Before
	public void setUp() throws Exception {
		server = SimpleSmtpServer.start(SMTP_PORT);
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	static class EmailChannelPropertiesTestImpl implements EmailChannelProperties {
		@Override
		public Properties getProperties() {
			Properties props = System.getProperties();
			props.put("mail.smtp.host", "localhost");
			props.setProperty("mail.smtp.port", "" + SMTP_PORT);
			props.setProperty("mail.smtp.sendpartial", "true");
			return props;
		}

		@Override
		public Authenticator getAuthenticator() {
			return null;
		}
	}

	@Test
	public void testSendEmail_expectMailDIliveredToVirtualServer() throws Exception {
		EmailSenderImpl fixture = new EmailSenderImpl();
		fixture.setEmailChannelProperties(new EmailChannelPropertiesTestImpl());
		EmailMessage emailMessage = new EmailMessage();
		emailMessage.setFromName("fromName");
		emailMessage.setFromAddress("from@name.com");
		emailMessage.setToAddress("to@name.com");
		emailMessage.setToName("toName");
		emailMessage.setSubject("ssubj");
		emailMessage.setBody("bbody");

		fixture.sendEmail(emailMessage);

		assertTrue(server.getReceivedEmailSize() == 1);
		@SuppressWarnings("rawtypes")
		Iterator emailIter = server.getReceivedEmail();
		SmtpMessage email = (SmtpMessage) emailIter.next();
		assertTrue(email.getHeaderValue("Subject").equals("ssubj"));
		assertTrue(email.getBody().equals("bbody"));
	}
}
