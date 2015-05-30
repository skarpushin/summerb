package org.summerb.easymail.api;

import java.util.Locale;

public interface EmailMessageTemplateResolver {
	EmailMessageTemplate get(String templateId, Locale locale);
}
