package org.summerb.utils.exceptions;

import org.summerb.approaches.jdbccrud.common.DtoBase;
import org.summerb.approaches.springmvc.utils.ExceptionDescription;

public class GenericServerErrorResult implements DtoBase {
	private static final long serialVersionUID = -3478702057346663837L;

	private String allErrorsMessage;
	private ExceptionDescription[] exceptions;

	public GenericServerErrorResult() {
	}

	public GenericServerErrorResult(String allErrorsMessage, ExceptionDescription[] exceptions) {
		this.allErrorsMessage = allErrorsMessage;
		this.exceptions = exceptions;
	}

	public String getAllErrorsMessage() {
		return allErrorsMessage;
	}

	public void setAllErrorsMessage(String allErrorsMessage) {
		this.allErrorsMessage = allErrorsMessage;
	}

	public ExceptionDescription[] getExceptions() {
		return exceptions;
	}

	public void setExceptions(ExceptionDescription[] exceptions) {
		this.exceptions = exceptions;
	}
}
