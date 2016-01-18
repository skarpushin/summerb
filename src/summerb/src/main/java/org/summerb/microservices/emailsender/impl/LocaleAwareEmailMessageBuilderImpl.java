package org.summerb.microservices.emailsender.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.CollectionUtils;
import org.summerb.microservices.emailsender.api.EmailMessageBuilder;
import org.summerb.microservices.emailsender.api.dto.EmailMessage;
import org.summerb.microservices.emailsender.api.dto.EmailTemplateParams;

import com.google.common.base.Preconditions;

/**
 * This impl doesn't actually implement email building procedure, but it knows
 * how to delegate to approriate builder according to current locale.
 * 
 * Make use of Spring's {@link LocaleContextHolder}
 * 
 * @author skarpushin
 * 
 */
public class LocaleAwareEmailMessageBuilderImpl implements EmailMessageBuilder {
	private Logger log = Logger.getLogger(getClass());

	private List<EmailMessageBuilder> localeSpecificBuilders;

	@Override
	public EmailMessage buildEmail(String fromAddress, String toAddress, EmailTemplateParams emailTemplateParams) {
		Preconditions.checkState(!CollectionUtils.isEmpty(localeSpecificBuilders),
				"Locale specific builders map is not provided");

		EmailMessageBuilder builder = getBuilder();

		return builder.buildEmail(fromAddress, toAddress, emailTemplateParams);
	}

	protected EmailMessageBuilder getBuilder() {
		Locale currentLocale = LocaleContextHolder.getLocale();

		// try full match
		for (EmailMessageBuilder entry : localeSpecificBuilders) {
			if (entry.getLocale().equals(currentLocale)) {
				return entry;
			}
		}

		// try language match
		for (EmailMessageBuilder entry : localeSpecificBuilders) {
			if (entry.getLocale().getLanguage().equals(currentLocale.getLanguage())) {
				return entry;
			}
		}

		// get something!
		log.warn(String.format("Was unable to find appropriate email builder for locale '%s', will peek the first one",
				currentLocale.toString()));
		return localeSpecificBuilders.get(0);
	}

	@Override
	public Locale getLocale() {
		return LocaleContextHolder.getLocale();
	}

	public List<EmailMessageBuilder> getLocaleSpecificBuilders() {
		if (localeSpecificBuilders == null) {
			localeSpecificBuilders = new ArrayList<EmailMessageBuilder>();
		}
		return localeSpecificBuilders;
	}

	public void setLocaleSpecificBuilders(List<EmailMessageBuilder> localeSpecificBuilders) {
		this.localeSpecificBuilders = localeSpecificBuilders;
	}

}
