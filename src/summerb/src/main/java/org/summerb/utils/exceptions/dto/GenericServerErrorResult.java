package org.summerb.utils.exceptions.dto;

import org.summerb.approaches.jdbccrud.common.DtoBase;

public class GenericServerErrorResult implements DtoBase {
	private static final long serialVersionUID = -3478702057346663837L;

	private String exc;
	private ExceptionInfo exceptionInfo;

	public GenericServerErrorResult() {
	}
	
	public GenericServerErrorResult(String allErrorsMessage, ExceptionInfo exceptionInfo) {
		this.exc = allErrorsMessage;
		this.exceptionInfo = exceptionInfo;
	}

	/**
	 * @return Exception message from whole chain
	 */
	public String getExc() {
		return exc;
	}

	public void setExc(String allErrorsMessage) {
		this.exc = allErrorsMessage;
	}

	public ExceptionInfo getExceptionInfo() {
		return exceptionInfo;
	}

	public void setExceptionInfo(ExceptionInfo exceptionInfo) {
		this.exceptionInfo = exceptionInfo;
	}
}
