package org.summerb.validation.errors;

import org.summerb.i18n.HasMessageArgsConverters;
import org.summerb.i18n.MessageArgConverter;
import org.summerb.i18n.MessageCodeMessageArgConverter;
import org.summerb.validation.ValidationError;

public class MustBeEqualsValidationError extends ValidationError implements HasMessageArgsConverters {
	private static final long serialVersionUID = 1269537536170054395L;

	/**
	 * @deprecated used only for serialization
	 */
	@Deprecated
	public MustBeEqualsValidationError() {

	}

	@SuppressWarnings("deprecation")
	public MustBeEqualsValidationError(String aMessageCode, String bMessageCode, String fieldToken) {
		super("validation.valuesMustBeEquals", fieldToken, aMessageCode, bMessageCode);
	}

	@Override
	public MessageArgConverter[] getMessageArgsConverters() {
		return new MessageArgConverter[] { MessageCodeMessageArgConverter.INSTANCE,
				MessageCodeMessageArgConverter.INSTANCE };
	}
}
