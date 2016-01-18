package org.summerb.microservices.emailsender.api;

import java.util.Locale;

import org.summerb.microservices.emailsender.api.dto.EmailMessage;
import org.summerb.microservices.emailsender.api.dto.EmailTemplateParams;

/**
 * Interface for interacting with specific SINGLE email template.
 * 
 * For example you need to periodicaly send some email. This builder will have
 * precompiled version of email tempalte and specific email instance might be
 * acquired by calling {@link #buildEmail(String, String, EmailTemplateParams)}
 * 
 * @author skarpushin
 * 
 */
public interface EmailMessageBuilder {

	/**
	 * Get locale this builder was created for
	 */
	Locale getLocale();

	/**
	 * Get specific email message
	 * 
	 * @param fromAddress
	 * @param toAddress
	 * @param emailTemplateParams
	 * @return
	 */
	EmailMessage buildEmail(String fromAddress, String toAddress, EmailTemplateParams emailTemplateParams);
}
