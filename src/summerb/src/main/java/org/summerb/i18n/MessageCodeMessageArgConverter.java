package org.summerb.i18n;

import org.springframework.context.MessageSource;

import com.google.common.base.Preconditions;

/**
 * This converter will get message arg and treat as a message code and translate
 * it
 * 
 * @author skarpushin
 * 
 */
public class MessageCodeMessageArgConverter extends MessageArgConverter {
	public static final MessageCodeMessageArgConverter INSTANCE = new MessageCodeMessageArgConverter();

	/**
	 * Prevent from instantiating this class and enforce to use same instance
	 * everytime
	 */
	private MessageCodeMessageArgConverter() {
	}

	@Override
	public String convert(Object arg, HasMessageCode hasMessageCode, MessageSource messageSource) {
		Preconditions.checkArgument(arg != null);
		Preconditions.checkArgument(arg instanceof String);
		return I18nUtils.getMessage(arg.toString(), null, messageSource);
	}

}
