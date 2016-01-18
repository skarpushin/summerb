package org.summerb.microservices.emailsender.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.summerb.microservices.emailsender.api.dto.EmailMessage;
import org.summerb.microservices.emailsender.api.dto.EmailTemplateParams;
import org.summerb.microservices.emailsender.api.dto.EmailTemplateParamsFactory;
import org.summerb.utils.stringtemplate.api.StringTemplateCompiler;
import org.summerb.utils.stringtemplate.impl.StringTemplateCompilerlImpl;

public class EmailBuilderImplTest {

	StringTemplateCompiler stringTemplateCompiler = new StringTemplateCompilerlImpl();

	@Test
	public void testBuildEmail_1() throws Exception {
		EmailMessageBuilderImpl fixture = EmailBuilderImplFactory.createEmailBuilderImplUS();
		EmailTemplateParams emailTemplateParams = EmailTemplateParamsFactory.createEmailTemplateParams();

		EmailMessage result = fixture.buildEmail("fromAddress", "toAddress", emailTemplateParams);

		assertNotNull(result);
		assertEquals("From: actualSender", result.getFromName());
		assertEquals("To: actualRecipient", result.getToName());
		assertEquals("Subject: bodyId", result.getSubject());
		assertEquals("Body: bodyMsg", result.getBody());
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
}