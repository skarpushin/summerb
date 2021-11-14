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

import org.summerb.utils.DtoBase;

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

	/**
	 * @return Exception message from whole chain
	 */
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
