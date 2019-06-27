package org.summerb.webappboilerplate.utils;

import java.io.Serializable;

/**
 * Provides simple description of exception able to be serialized
 * 
 * @author sergeyk
 * 
 */
public class ExceptionDescription implements Serializable {
	private static final long serialVersionUID = 2895147359877289381L;

	private String className;
	private String message;

	public ExceptionDescription() {
	}

	public ExceptionDescription(String className, String message) {
		this.className = className;
		this.message = message;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
