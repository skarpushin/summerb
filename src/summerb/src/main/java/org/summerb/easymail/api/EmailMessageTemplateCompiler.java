package org.summerb.easymail.api;

import org.summerb.easymail.api.dto.EmailMessage;

public interface EmailMessageTemplateCompiler {
	EmailMessageTemplate compile(EmailMessage template);
}
