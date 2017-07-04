package org.summerb.utils.exceptions.dto;

import java.io.Serializable;

import org.summerb.approaches.i18n.HasMessageArgs;
import org.summerb.approaches.i18n.HasMessageCode;

public class ExceptionInfo implements Serializable {
	private static final long serialVersionUID = 1857628060664652023L;

	private String exceptionClassName;
	private String message;

	private String messageCode;
	private Object[] messageArgs;
	private Serializable errorDescriptionObject;

	private ExceptionInfo cause;

	/**
	 * @Deprecated It's created for IO purposes only
	 */
	public ExceptionInfo() {
	}

	public ExceptionInfo(Throwable t) {
		exceptionClassName = t.getClass().getName();
		message = t.getMessage();

		if (t instanceof HasMessageCode) {
			messageCode = ((HasMessageCode) t).getMessageCode();
		}

		if (t instanceof HasMessageArgs) {
			messageArgs = ((HasMessageArgs) t).getMessageArgs();
		}

		if (t instanceof HasErrorDescriptionObject) {
			errorDescriptionObject = ((HasErrorDescriptionObject<?>) t).getErrorDescriptionObject();
		}

		if (t.getCause() != null && t.getCause() != t) {
			cause = new ExceptionInfo(t.getCause());
		}
	}

	public boolean isExceptionClass(Class<? extends Throwable> clazz) {
		try {
			Class<?> current = Class.forName(exceptionClassName);
			return clazz.isAssignableFrom(current);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public String getExceptionClassName() {
		return exceptionClassName;
	}

	public void setExceptionClassName(String exceptionClassName) {
		this.exceptionClassName = exceptionClassName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public Object[] getMessageArgs() {
		return messageArgs;
	}

	public void setMessageArgs(Object[] messageArgs) {
		this.messageArgs = messageArgs;
	}

	public Serializable getErrorDescriptionObject() {
		return errorDescriptionObject;
	}

	public void setErrorDescriptionObject(Serializable errorDescriptionObject) {
		this.errorDescriptionObject = errorDescriptionObject;
	}

	public ExceptionInfo getCause() {
		return cause;
	}

	public void setCause(ExceptionInfo cause) {
		this.cause = cause;
	}
}
