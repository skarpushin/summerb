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
package org.summerb.security.api.exceptions;

import org.summerb.i18n.HasMessageArgs;
import org.summerb.i18n.HasMessageArgsConverters;
import org.summerb.i18n.HasMessageCode;
import org.summerb.i18n.MessageArgConverter;
import org.summerb.security.api.dto.NotAuthorizedResult;
import org.summerb.utils.exceptions.HasErrorDescriptionObject;

import com.google.common.base.Preconditions;

/**
 * Thrown when user is not authorized to perform operation in general or
 * applicable to specific subject
 * 
 * @author sergey.karpushin
 * 
 */
@SuppressWarnings("serial")
public class NotAuthorizedException extends Exception implements HasMessageCode, HasMessageArgs,
		HasMessageArgsConverters, HasErrorDescriptionObject<NotAuthorizedResult> {

	private final NotAuthorizedResult result;

	public NotAuthorizedException(NotAuthorizedResult result) {
		super(result.getOperationMessageCode());
		Preconditions.checkArgument(result != null);
		Preconditions.checkArgument(result.getOperationMessageCode() != null);
		Preconditions.checkArgument(result.getMessageCode() != null);

		this.result = result;
	}

	public NotAuthorizedException(NotAuthorizedResult result, Throwable cause) {
		super(result.getOperationMessageCode(), cause);
		Preconditions.checkArgument(result != null);
		Preconditions.checkArgument(result.getOperationMessageCode() != null);
		Preconditions.checkArgument(result.getMessageCode() != null);

		this.result = result;
	}

	public NotAuthorizedException(String userName, String operationMessageCode) {
		super(operationMessageCode);
		Preconditions.checkArgument(operationMessageCode != null);
		result = new NotAuthorizedResult(userName, operationMessageCode);
	}

	public NotAuthorizedException(String userName, String operationMessageCode, String subjectTitle) {
		super(operationMessageCode);
		Preconditions.checkArgument(operationMessageCode != null);
		result = new NotAuthorizedResult(userName, operationMessageCode, subjectTitle);
	}

	@Override
	public String getMessageCode() {
		return result.getMessageCode();
	}

	@Override
	public MessageArgConverter[] getMessageArgsConverters() {
		return result.getMessageArgsConverters();
	}

	@Override
	public Object[] getMessageArgs() {
		return result.getMessageArgs();
	}

	public NotAuthorizedResult getResult() {
		return result;
	}

	public String getOperationMessageCode() {
		return result.getOperationMessageCode();
	}

	@Override
	public NotAuthorizedResult getErrorDescriptionObject() {
		return result;
	}

}
