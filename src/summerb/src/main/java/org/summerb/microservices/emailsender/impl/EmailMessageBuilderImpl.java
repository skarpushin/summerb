package org.summerb.microservices.emailsender.impl;

import java.util.Locale;

import org.summerb.microservices.emailsender.api.EmailMessageBuilder;
import org.summerb.microservices.emailsender.api.dto.EmailMessage;
import org.summerb.microservices.emailsender.api.dto.EmailTemplateParams;
import org.summerb.utils.stringtemplate.api.StringTemplate;

import com.google.common.base.Preconditions;

public class EmailMessageBuilderImpl implements EmailMessageBuilder {
	private Locale locale;
	private StringTemplate fromNameTemplate;
	private StringTemplate toNameTemplate;
	private StringTemplate subjectTemplate;
	private StringTemplate bodyTemplate;

	public EmailMessageBuilderImpl() {
	}

	private void checkState() {
		Preconditions.checkState(locale != null);
		Preconditions.checkState(toNameTemplate != null);
		Preconditions.checkState(fromNameTemplate != null);
		Preconditions.checkState(subjectTemplate != null);
		Preconditions.checkState(bodyTemplate != null);
	}

	@Override
	public EmailMessage buildEmail(String fromAddress, String toAddress, EmailTemplateParams emailTemplateParams) {
		checkState();

		Preconditions.checkArgument(fromAddress != null);
		Preconditions.checkArgument(toAddress != null);
		Preconditions.checkArgument(emailTemplateParams != null);

		EmailMessage ret = new EmailMessage();
		ret.setFromName(fromNameTemplate.applyTo(emailTemplateParams));
		ret.setFromAddress(fromAddress);

		ret.setToName(toNameTemplate.applyTo(emailTemplateParams));
		ret.setToAddress(toAddress);

		ret.setSubject(subjectTemplate.applyTo(emailTemplateParams));

		ret.setBody(bodyTemplate.applyTo(emailTemplateParams));

		return ret;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public StringTemplate getFromNameTemplate() {
		return fromNameTemplate;
	}

	public void setFromNameTemplate(StringTemplate fromNameTemplate) {
		this.fromNameTemplate = fromNameTemplate;
	}

	public StringTemplate getToNameTemplate() {
		return toNameTemplate;
	}

	public void setToNameTemplate(StringTemplate toNameTemplate) {
		this.toNameTemplate = toNameTemplate;
	}

	public StringTemplate getSubjectTemplate() {
		return subjectTemplate;
	}

	public void setSubjectTemplate(StringTemplate subjectTemplate) {
		this.subjectTemplate = subjectTemplate;
	}

	public StringTemplate getBodyTemplate() {
		return bodyTemplate;
	}

	public void setBodyTemplate(StringTemplate bodyTemplate) {
		this.bodyTemplate = bodyTemplate;
	}

}
