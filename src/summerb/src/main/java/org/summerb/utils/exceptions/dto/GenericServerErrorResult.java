package org.summerb.utils.exceptions.dto;

import org.summerb.approaches.jdbccrud.common.DtoBase;

public class GenericServerErrorResult implements DtoBase {
	private static final long serialVersionUID = -3478702057346663837L;

	private String allErrorsMessage;
	private ExceptionInfo exceptionInfo;

	public GenericServerErrorResult() {
	}

	public GenericServerErrorResult(String allErrorsMessage, ExceptionInfo exceptionInfo) {
		this.allErrorsMessage = allErrorsMessage;
		this.exceptionInfo = exceptionInfo;
	}

	public String getAllErrorsMessage() {
		return allErrorsMessage;
	}

	public void setAllErrorsMessage(String allErrorsMessage) {
		this.allErrorsMessage = allErrorsMessage;
	}

	public ExceptionInfo getExceptionInfo() {
		return exceptionInfo;
	}

	public void setExceptionInfo(ExceptionInfo exceptionInfo) {
		this.exceptionInfo = exceptionInfo;
	}
}
