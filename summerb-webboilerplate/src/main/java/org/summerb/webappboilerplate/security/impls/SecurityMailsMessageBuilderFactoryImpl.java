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
package org.summerb.webappboilerplate.security.impls;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.summerb.easycrud.api.dto.EntityChangedEvent;
import org.summerb.easycrud.api.dto.EntityChangedEvent.ChangeType;
import org.summerb.email.api.EmailMessageBuilder;
import org.summerb.email.impl.EmailMessageBuilderImpl;
import org.summerb.email.impl.LocaleAwareEmailMessageBuilderImpl;
import org.summerb.minicms.api.ArticleService;
import org.summerb.minicms.api.dto.Article;
import org.summerb.stringtemplate.api.StringTemplateCompiler;
import org.summerb.stringtemplate.impl.StringTemplateStaticImpl;
import org.summerb.users.api.dto.User;
import org.summerb.webappboilerplate.security.apis.SecurityMailsMessageBuilderFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class SecurityMailsMessageBuilderFactoryImpl implements SecurityMailsMessageBuilderFactory, InitializingBean {
	private Logger log = LogManager.getLogger(getClass());

	public static final String EMAIL_REGISTRATION_CONFIRMATION_REQUEST = "email-registration-confirmation-request";
	public static final String EMAIL_PASSWORD_RESET_REQUEST = "email-reset-password-request";

	private ArticleService articleService;
	private StringTemplateCompiler stringTemplateCompiler;
	private EventBus eventBus;

	private User registrationEmailSender;
	private String emailSenderAddress;
	private String emailSenderName;
	private volatile EmailMessageBuilder registrationEmailBuilder;
	private volatile EmailMessageBuilder passwordResetEmailBuilder;

	@Override
	public void afterPropertiesSet() throws Exception {
		eventBus.register(this);
	}

	@Subscribe
	public void onEntityChange(EntityChangedEvent<Article> evt) {
		if (!evt.isTypeOf(Article.class)) {
			return;
		}
		if (evt.getChangeType() != ChangeType.UPDATED && evt.getChangeType() != ChangeType.REMOVED) {
			return;
		}

		if (EMAIL_PASSWORD_RESET_REQUEST.equals(evt.getValue().getArticleKey())) {
			passwordResetEmailBuilder = null;
		}

		if (EMAIL_REGISTRATION_CONFIRMATION_REQUEST.equals(evt.getValue().getArticleKey())) {
			registrationEmailBuilder = null;
		}
	}

	@Override
	public EmailMessageBuilder createEmailMessageBuilderFromArticle(String articleKey, User sender) {
		try {
			Map<Locale, Article> options = articleService.findArticleLocalizations(articleKey);
			if (options == null || options.size() == 0) {
				log.error("Emails will not be sent! Article not found: " + articleKey);
			}

			LocaleAwareEmailMessageBuilderImpl ret = new LocaleAwareEmailMessageBuilderImpl();
			for (Entry<Locale, Article> entry : options.entrySet()) {
				EmailMessageBuilderImpl builder = new EmailMessageBuilderImpl();
				builder.setLocale(entry.getKey());

				builder.setFromNameTemplate(new StringTemplateStaticImpl(sender.getDisplayName()));
				builder.setToNameTemplate(stringTemplateCompiler.compile("${to.displayName}"));
				builder.setSubjectTemplate(new StringTemplateStaticImpl(entry.getValue().getTitle()));
				builder.setBodyTemplate(stringTemplateCompiler.compile(entry.getValue().getContent()));

				ret.getLocaleSpecificBuilders().add(builder);
			}

			return ret;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to create email message builder using article id: " + articleKey, t);
		}
	}

	@Override
	public User getAccountOperationsSender() {
		if (registrationEmailSender == null) {
			registrationEmailSender = new User();
			registrationEmailSender.setEmail(emailSenderAddress);
			registrationEmailSender.setDisplayName(emailSenderName);
		}
		return registrationEmailSender;
	}

	@Override
	public EmailMessageBuilder getRegistrationEmailBuilder() {
		if (registrationEmailBuilder == null) {
			registrationEmailBuilder = createEmailMessageBuilderFromArticle(EMAIL_REGISTRATION_CONFIRMATION_REQUEST,
					registrationEmailSender);
		}
		return registrationEmailBuilder;
	}

	@Override
	public EmailMessageBuilder getPasswordResetEmailBuilder() {
		if (passwordResetEmailBuilder == null) {
			passwordResetEmailBuilder = createEmailMessageBuilderFromArticle(EMAIL_PASSWORD_RESET_REQUEST,
					registrationEmailSender);
		}
		return passwordResetEmailBuilder;
	}

	public StringTemplateCompiler getStringTemplateCompiler() {
		return stringTemplateCompiler;
	}

	@Autowired
	public void setStringTemplateCompiler(StringTemplateCompiler stringTemplateCompiler) {
		this.stringTemplateCompiler = stringTemplateCompiler;
	}

	@Autowired
	public void setArticleService(ArticleService articleViewerService) {
		this.articleService = articleViewerService;
	}

	public String getEmailSenderAddress() {
		return emailSenderAddress;
	}

	@Required
	public void setEmailSenderAddress(String emailSenderAddress) {
		this.emailSenderAddress = emailSenderAddress;
	}

	public String getEmailSenderName() {
		return emailSenderName;
	}

	@Required
	public void setEmailSenderName(String emailSenderName) {
		this.emailSenderName = emailSenderName;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	@Autowired
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

}
