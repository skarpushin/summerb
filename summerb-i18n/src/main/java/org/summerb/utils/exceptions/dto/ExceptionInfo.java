/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.utils.exceptions.dto;

import java.io.Serializable;

import org.summerb.i18n.HasMessageArgs;
import org.summerb.i18n.HasMessageCode;
import org.summerb.utils.DtoBase;
import org.summerb.utils.exceptions.HasErrorDescriptionObject;

public class ExceptionInfo implements DtoBase {
	private static final long serialVersionUID = 1857628060664652023L;

	private String exceptionClassName;
	private String message;

	private String messageCode;
	private Object[] messageArgs;
	private Serializable errorDescriptionObject;

	private ExceptionInfo cause;

	/**
	 * @deprecated It's created for IO purposes only
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
