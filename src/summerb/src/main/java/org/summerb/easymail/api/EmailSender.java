package org.summerb.easymail.api;

import java.util.Locale;

public interface EmailSender {
	void send(String templateId, Locale locale, Object data);
}
