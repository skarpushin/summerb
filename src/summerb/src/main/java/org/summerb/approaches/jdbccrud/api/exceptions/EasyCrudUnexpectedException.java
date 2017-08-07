package org.summerb.approaches.jdbccrud.api.exceptions;

import org.summerb.approaches.i18n.HasMessageArgs;
import org.summerb.approaches.i18n.HasMessageArgsConverters;
import org.summerb.approaches.i18n.HasMessageCode;
import org.summerb.approaches.i18n.MessageArgConverter;
import org.summerb.approaches.i18n.MessageCodeMessageArgConverter;

/**
 *  
 * @author sergey.karpushin
 *
 */
public class EasyCrudUnexpectedException extends RuntimeException
		implements HasMessageCode, HasMessageArgs, HasMessageArgsConverters {
	private static final long serialVersionUID = 5053151069728043611L;
	private String messageCode;
	private String entityMessageCode;

	public EasyCrudUnexpectedException(String messageCode, String entityMessageCode, Throwable cause) {
		super("Unexpected exception, code = " + messageCode + ", entity = " + entityMessageCode, cause);
		this.messageCode = messageCode;
		this.entityMessageCode = entityMessageCode;
	}

	@Override
	public MessageArgConverter[] getMessageArgsConverters() {
		return new MessageArgConverter[] { MessageCodeMessageArgConverter.INSTANCE };
	}

	@Override
	public Object[] getMessageArgs() {
		return new Object[] { entityMessageCode };
	}

	@Override
	public String getMessageCode() {
		return messageCode;
	}

}
