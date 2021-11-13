/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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

import java.util.Locale;

import org.summerb.email.api.EmailMessageBuilder;
import org.summerb.email.api.dto.EmailMessage;
import org.summerb.email.api.dto.EmailTemplateParams;
import org.summerb.stringtemplate.api.StringTemplate;

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
