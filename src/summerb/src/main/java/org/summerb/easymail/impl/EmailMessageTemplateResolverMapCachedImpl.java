package org.summerb.easymail.impl;

import java.util.HashMap;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.easymail.api.EmailMessageRepository;
import org.summerb.easymail.api.EmailMessageTemplate;
import org.summerb.easymail.api.EmailMessageTemplateCompiler;
import org.summerb.easymail.api.EmailMessageTemplateResolver;
import org.summerb.easymail.api.dto.EmailMessage;

public class EmailMessageTemplateResolverMapCachedImpl implements EmailMessageTemplateResolver {
	private EmailMessageRepository emailMessageRepository;
	private EmailMessageTemplateCompiler emailMessageTemplateCompiler;
	private HashMap<EmailMessagePermutationId, EmailMessageTemplate> emailMessageTemplateCache = new HashMap<EmailMessagePermutationId, EmailMessageTemplate>();

	@Override
	public EmailMessageTemplate get(String templateId, Locale locale) {
		EmailMessagePermutationId key = new EmailMessagePermutationId(templateId, locale);
		EmailMessageTemplate emailMessageTemplate = emailMessageTemplateCache.get(key);
		if (emailMessageTemplate == null) {
			EmailMessage emailMessage = emailMessageRepository.get(templateId, locale);
			emailMessageTemplate = emailMessageTemplateCompiler.compile(emailMessage);
			emailMessageTemplateCache.put(key, emailMessageTemplate);
		}
		return emailMessageTemplate;
	}

	public EmailMessageRepository getEmailMessageRepository() {
		return emailMessageRepository;
	}

	@Autowired
	public void setEmailMessageRepository(EmailMessageRepository emailMessageRepository) {
		this.emailMessageRepository = emailMessageRepository;
	}

	public EmailMessageTemplateCompiler getEmailMessageTemplateCompiler() {
		return emailMessageTemplateCompiler;
	}

	@Autowired
	public void setEmailMessageTemplateCompiler(EmailMessageTemplateCompiler emailMessageTemplateCompiler) {
		this.emailMessageTemplateCompiler = emailMessageTemplateCompiler;
	}
}
