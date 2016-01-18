package org.summerb.approaches.jdbccrud.common;

import java.sql.DataTruncation;

import org.summerb.approaches.i18n.HasMessageCode;

public class ServiceDataTruncationException extends DataTruncation implements HasMessageCode {
	private static final long serialVersionUID = -8091580877575496130L;
	private String fieldTokenBeingTruncated;
	private String message;
	private Throwable cause;

	/**
	 * @deprecated used only for IO purposes
	 */
	@Deprecated
	public ServiceDataTruncationException() {
		super(-1, false, false, 0, 0);
	}

	public ServiceDataTruncationException(String fieldTokenBeingTruncated, Throwable cause) {
		super(-1, false, false, 0, 0);
		this.fieldTokenBeingTruncated = fieldTokenBeingTruncated;

		message = "Data was too long for field '" + fieldTokenBeingTruncated + "'";
		this.cause = cause;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public Throwable getCause() {
		return cause;
	}

	public static RuntimeException envelopeFor(String fieldName, Throwable cause) {
		DataTruncation dataTruncation = new ServiceDataTruncationException(fieldName, cause);
		throw new RuntimeException("Data truncation error was detected", dataTruncation);
	}

	@Override
	public String getMessageCode() {
		return "exception.dao.dataTruncationError";
	}

	public String getFieldTokenBeingTruncated() {
		return fieldTokenBeingTruncated;
	}
}
