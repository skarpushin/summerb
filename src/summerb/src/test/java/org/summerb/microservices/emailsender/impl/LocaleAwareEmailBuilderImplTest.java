package org.summerb.microservices.emailsender.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.summerb.microservices.emailsender.api.EmailMessageBuilder;
import org.summerb.microservices.emailsender.api.dto.EmailMessage;
import org.summerb.microservices.emailsender.api.dto.EmailTemplateParamsFactory;

public class LocaleAwareEmailBuilderImplTest {

	@Test
	public void testGetBuilder_expectWillPeekRightLocale() throws Exception {
		// setup preconditions
		List<EmailMessageBuilder> map = new ArrayList<EmailMessageBuilder>();

		EmailMessageBuilderImpl enUsEmailBuilderImpl = getBuilderFor("en", "US");
		map.add(enUsEmailBuilderImpl);

		EmailMessageBuilderImpl ruRuEmailBuilderImpl = getBuilderFor("ru", "RU");
		map.add(ruRuEmailBuilderImpl);

		LocaleAwareEmailMessageBuilderImpl fixture = new LocaleAwareEmailMessageBuilderImpl();
		fixture.setLocaleSpecificBuilders(map);

		LocaleContextHolder.setLocale(new Locale("ru", "RU"));

		// test
		EmailMessageBuilder result = fixture.getBuilder();

		// check
		assertTrue(ruRuEmailBuilderImpl == result);
	}

	protected EmailMessageBuilderImpl getBuilderFor(String language, String country) {
		EmailMessageBuilderImpl enUsEmailBuilderImpl = new EmailMessageBuilderImpl();
		enUsEmailBuilderImpl.setLocale(new Locale(language, country));
		return enUsEmailBuilderImpl;
	}

	@Test
	public void testGetBuilder_expectWillPeekRightLocaleByLanguage() throws Exception {
		// setup preconditions
		List<EmailMessageBuilder> map = new ArrayList<EmailMessageBuilder>();

		EmailMessageBuilderImpl enGbEmailBuilderImpl = getBuilderFor("en", "GB");
		map.add(enGbEmailBuilderImpl);

		EmailMessageBuilderImpl ruRuEmailBuilderImpl = getBuilderFor("ru", "RU");
		map.add(ruRuEmailBuilderImpl);

		LocaleAwareEmailMessageBuilderImpl fixture = new LocaleAwareEmailMessageBuilderImpl();
		fixture.setLocaleSpecificBuilders(map);

		LocaleContextHolder.setLocale(new Locale("en", "US"));

		// test
		EmailMessageBuilder result = fixture.getBuilder();

		// check
		assertTrue(enGbEmailBuilderImpl == result);
	}

	@Test
	public void testGetBuilder_expectWillPeekAnyLocale() throws Exception {
		// setup preconditions
		List<EmailMessageBuilder> map = new ArrayList<EmailMessageBuilder>();

		EmailMessageBuilderImpl ruRuEmailBuilderImpl = getBuilderFor("ru", "RU");
		map.add(ruRuEmailBuilderImpl);

		EmailMessageBuilderImpl enGbEmailBuilderImpl = getBuilderFor("en", "GB");
		map.add(enGbEmailBuilderImpl);

		LocaleAwareEmailMessageBuilderImpl fixture = new LocaleAwareEmailMessageBuilderImpl();
		fixture.setLocaleSpecificBuilders(map);

		LocaleContextHolder.setLocale(new Locale("fr", "FR"));

		// test
		EmailMessageBuilder result = fixture.getBuilder();

		// check
		assertTrue(ruRuEmailBuilderImpl == result);
	}

	@Test(expected = IllegalStateException.class)
	public void testBuildEMail_expectException() throws Exception {
		LocaleAwareEmailMessageBuilderImpl fixture = new LocaleAwareEmailMessageBuilderImpl();
		fixture.buildEmail(null, null, null);
		fail();
	}

	@Test
	public void testBuildEMail_expectEnUsMessage() throws Exception {
		// setup preconditions
		List<EmailMessageBuilder> map = new ArrayList<EmailMessageBuilder>();
		map.add(EmailBuilderImplFactory.createEmailBuilderImplUS());
		map.add(EmailBuilderImplFactory.createEmailBuilderImplRU());

		LocaleAwareEmailMessageBuilderImpl fixture = new LocaleAwareEmailMessageBuilderImpl();
		fixture.setLocaleSpecificBuilders(map);

		LocaleContextHolder.setLocale(new Locale("fr", "FR"));

		// test
		EmailMessage result = fixture.buildEmail("fromAddr", "toAddr",
				EmailTemplateParamsFactory.createEmailTemplateParams());

		// check
		assertNotNull(result);
		assertEquals("From: actualSender", result.getFromName());
		assertEquals("To: actualRecipient", result.getToName());
		assertEquals("Subject: bodyId", result.getSubject());
		assertEquals("Body: bodyMsg", result.getBody());
	}

	@Test
	public void testBuildEMail_expectRuRuMessage() throws Exception {
		// setup preconditions
		List<EmailMessageBuilder> map = new ArrayList<EmailMessageBuilder>();
		map.add(EmailBuilderImplFactory.createEmailBuilderImplRU());
		map.add(EmailBuilderImplFactory.createEmailBuilderImplUS());

		LocaleAwareEmailMessageBuilderImpl fixture = new LocaleAwareEmailMessageBuilderImpl();
		fixture.setLocaleSpecificBuilders(map);

		LocaleContextHolder.setLocale(new Locale("fr", "FR"));

		// test
		EmailMessage result = fixture.buildEmail("fromAddr", "toAddr",
				EmailTemplateParamsFactory.createEmailTemplateParams());

		// check
		assertNotNull(result);
		assertEquals("От: actualSender", result.getFromName());
		assertEquals("К: actualRecipient", result.getToName());
		assertEquals("Тема: bodyId", result.getSubject());
		assertEquals("Тело: bodyMsg", result.getBody());
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
}