package org.summerb.utils.exceptions;

import org.summerb.utils.exceptions.dto.ExceptionInfo;
import org.summerb.utils.exceptions.dto.HasErrorDescriptionObject;

/**
 * This exception is supposed to be created when we receive ExceptionInfo from
 * the remote system. It means exception happened in remote system,
 * 
 * @author sergeyk
 *
 */
public class ExceptionInfoReceivedException extends RuntimeException
		implements HasErrorDescriptionObject<ExceptionInfo> {
	private static final long serialVersionUID = 2846267644114060942L;
	private final ExceptionInfo exceptionInfo;

	public ExceptionInfoReceivedException(ExceptionInfo exceptionInfo) {
		super("Received ExceptionInfo from remote system");
		this.exceptionInfo = exceptionInfo;
	}
	
	public ExceptionInfoReceivedException(ExceptionInfo exceptionInfo, Throwable cause) {
		super("Received ExceptionInfo from remote system", cause);
		this.exceptionInfo = exceptionInfo;
	}

	@Override
	public ExceptionInfo getErrorDescriptionObject() {
		return exceptionInfo;
	}
}
