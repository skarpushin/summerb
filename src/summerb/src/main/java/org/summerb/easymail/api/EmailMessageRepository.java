package org.summerb.easymail.api;

import java.util.Locale;

import org.summerb.easymail.api.dto.EmailMessage;

public interface EmailMessageRepository {
	EmailMessage get(String templateId, Locale locale);
}
