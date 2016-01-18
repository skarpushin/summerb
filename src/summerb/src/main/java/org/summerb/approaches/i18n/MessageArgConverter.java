package org.summerb.approaches.i18n;

import org.springframework.context.MessageSource;

public abstract class MessageArgConverter {
	public abstract String convert(Object arg, HasMessageCode hasMessageCode, MessageSource messageSource);
}
