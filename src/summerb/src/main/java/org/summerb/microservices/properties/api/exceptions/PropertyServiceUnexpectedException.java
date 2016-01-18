package org.summerb.microservices.properties.api.exceptions;

public class PropertyServiceUnexpectedException extends RuntimeException {
	private static final long serialVersionUID = -755441433588285795L;

	public PropertyServiceUnexpectedException() {
	}

	public PropertyServiceUnexpectedException(String technicalMessage, Throwable cause) {
		super(technicalMessage, cause);
	}

	public PropertyServiceUnexpectedException(String technicalMessage) {
		super(technicalMessage);
	}
}
