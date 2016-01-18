package org.summerb.microservices.emailsender.impl;

import java.util.Locale;

import org.summerb.utils.stringtemplate.api.StringTemplateCompiler;
import org.summerb.utils.stringtemplate.impl.StringTemplateCompilerlImpl;

public class EmailBuilderImplFactory {
	static StringTemplateCompiler stringTemplateCompiler = new StringTemplateCompilerlImpl();

	public static EmailMessageBuilderImpl createEmailBuilderImplUS() {
		EmailMessageBuilderImpl fixture = new EmailMessageBuilderImpl();

		fixture.setFromNameTemplate(stringTemplateCompiler.compile("From: ${from.name}"));
		fixture.setToNameTemplate(stringTemplateCompiler.compile("To: ${to.name}"));
		fixture.setSubjectTemplate(stringTemplateCompiler.compile("Subject: ${body.id}"));
		fixture.setBodyTemplate(stringTemplateCompiler.compile("Body: ${body.msg}"));
		fixture.setLocale(Locale.US);

		return fixture;
	}

	public static EmailMessageBuilderImpl createEmailBuilderImplRU() {
		EmailMessageBuilderImpl fixture = new EmailMessageBuilderImpl();

		fixture.setFromNameTemplate(stringTemplateCompiler.compile("От: ${from.name}"));
		fixture.setToNameTemplate(stringTemplateCompiler.compile("К: ${to.name}"));
		fixture.setSubjectTemplate(stringTemplateCompiler.compile("Тема: ${body.id}"));
		fixture.setBodyTemplate(stringTemplateCompiler.compile("Тело: ${body.msg}"));
		fixture.setLocale(new Locale("ru", "RU"));

		return fixture;
	}

}