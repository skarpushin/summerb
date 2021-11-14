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
package org.summerb.utils.exceptions;

import org.summerb.utils.exceptions.dto.ExceptionInfo;

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
