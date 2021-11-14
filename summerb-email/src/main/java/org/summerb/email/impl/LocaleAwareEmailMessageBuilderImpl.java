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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.CollectionUtils;
import org.summerb.email.api.EmailMessageBuilder;
import org.summerb.email.api.dto.EmailMessage;
import org.summerb.email.api.dto.EmailTemplateParams;

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
	private Logger log = LogManager.getLogger(getClass());

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
