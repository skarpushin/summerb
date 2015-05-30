package org.summerb.easymail.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.easymail.api.EmailMessageTemplate;
import org.summerb.easymail.api.EmailMessageTemplateCompiler;
import org.summerb.easymail.api.dto.EmailMessage;
import org.summerb.stringtemplate.api.StringTemplate;
import org.summerb.stringtemplate.api.StringTemplateCompiler;

public class EmailMessageTemplateCompilerImpl implements EmailMessageTemplateCompiler {
	private StringTemplateCompiler stringTemplateCompiler;

	@Autowired
	public void setStringTemplateCompiler(StringTemplateCompiler stringTemplateCompiler) {
		this.stringTemplateCompiler = stringTemplateCompiler;
	}

	@Override
	public EmailMessageTemplate compile(EmailMessage emailMessage) {
		try {
			StringTemplate subjectStringTemplate = stringTemplateCompiler.compile(emailMessage.getSubject());
			StringTemplate bodyStringTemplate = stringTemplateCompiler.compile(emailMessage.getBody());
			return new EmailMessageTemplateImpl(subjectStringTemplate, bodyStringTemplate);
		} catch (Throwable t) {
			throw new RuntimeException("Failed to compile template", t);
		}
	}

	private static class EmailMessageTemplateImpl implements EmailMessageTemplate {
		private StringTemplate subjectStringTemplate;
		private StringTemplate bodyStringTemplate;

		EmailMessageTemplateImpl(StringTemplate subjectCompiledTemplate, StringTemplate bodyStringTemplate) {
			this.subjectStringTemplate = subjectCompiledTemplate;
			this.bodyStringTemplate = bodyStringTemplate;
		}

		@Override
		public EmailMessage applyTo(Object data) {
			try {
				EmailMessage emailMessage = new EmailMessage();
				emailMessage.setSubject(subjectStringTemplate.applyTo(data));
				emailMessage.setBody(bodyStringTemplate.applyTo(data));
				return emailMessage;
			} catch (Throwable t) {
				throw new RuntimeException("Failed to create EmailMessage based on template", t);
			}

		}
	}
}
