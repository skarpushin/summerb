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

import org.summerb.i18n.HasMessageArgs;
import org.summerb.i18n.HasMessageCode;

public class GenericRuntimeException extends RuntimeException implements HasMessageCode, HasMessageArgs {
	private static final long serialVersionUID = 5911368838530147923L;
	private Object[] messageArgs;

	public GenericRuntimeException(String messageCode) {
		super(messageCode);
	}

	public GenericRuntimeException(String messageCode, Throwable cause, Object... messageArgs) {
		super(messageCode, cause);
		this.messageArgs = messageArgs;
	}

	@Override
	public String getMessageCode() {
		return getMessage();
	}

	@Override
	public Object[] getMessageArgs() {
		return messageArgs;
	}

}
