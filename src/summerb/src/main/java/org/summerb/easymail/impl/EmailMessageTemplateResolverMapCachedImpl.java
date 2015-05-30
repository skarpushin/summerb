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

	private static class EmailMessagePermutationId {
		private String templateId;
		private Locale locale;

		public EmailMessagePermutationId(String templateId, Locale locale) {
			this.templateId = templateId;
			this.locale = locale;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((locale == null) ? 0 : locale.hashCode());
			result = prime * result + ((templateId == null) ? 0 : templateId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			EmailMessagePermutationId other = (EmailMessagePermutationId) obj;
			if (locale == null) {
				if (other.locale != null)
					return false;
			} else if (!locale.equals(other.locale))
				return false;
			if (templateId == null) {
				if (other.templateId != null)
					return false;
			} else if (!templateId.equals(other.templateId))
				return false;
			return true;
		}
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
