package org.summerb.utils.exceptions;

import org.summerb.approaches.i18n.HasMessageArgs;
import org.summerb.approaches.i18n.HasMessageCode;

public class GenericRuntimeException extends RuntimeException implements HasMessageCode, HasMessageArgs {
	private static final long serialVersionUID = 5911368838530147923L;
	private Object[] messageArgs;

	public GenericRuntimeException(String messageCode) {
		super(messageCode);
	}

	public GenericRuntimeException(String messageCode, Throwable cause, Object... messageArgs) {
		super(messageCode, cause);
		this.messageArgs = messageArgs;
	}

	@Override
	public String getMessageCode() {
		return getMessage();
	}

	@Override
	public Object[] getMessageArgs() {
		return messageArgs;
	}

}
