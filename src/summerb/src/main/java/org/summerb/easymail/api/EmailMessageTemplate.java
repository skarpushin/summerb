package org.summerb.easymail.api;

import org.summerb.easymail.api.dto.EmailMessage;

public interface EmailMessageTemplate {
	EmailMessage applyTo(Object data);
}
