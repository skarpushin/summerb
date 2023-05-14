/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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
package org.summerb.security.api.dto;

import org.springframework.util.StringUtils;
import org.summerb.i18n.HasMessageArgs;
import org.summerb.i18n.HasMessageArgsConverters;
import org.summerb.i18n.HasMessageCode;
import org.summerb.i18n.MessageArgConverter;
import org.summerb.i18n.MessageCodeMessageArgConverter;
import org.summerb.utils.DtoBase;

import com.google.common.base.Preconditions;

/**
 * This DTO is used to store authorization failure information. It's designed to
 * be serialized and sent to front-end as a message or as a json or in other
 * serializable form.
 * 
 * @author sergeyk
 *
 */
public class NotAuthorizedResult implements DtoBase, HasMessageCode, HasMessageArgs, HasMessageArgsConverters {
	private static final transient long serialVersionUID = 1122164433294017483L;

	public static final String SECURITY_AUTHORIZATION_MISSING_ON_SUBJECT = "security.authorization.missingOnSubject";
	public static final String SECURITY_AUTHORIZATION_MISSING = "security.authorization.missing";

	private static final transient MessageArgConverter[] messageArgsConverters = new MessageArgConverter[] { null,
			MessageCodeMessageArgConverter.INSTANCE, null };

	private String userName;
	private String operationMessageCode;
	private String subjectTitle;
	private String messageCode;

	/**
	 * @deprecated used only for IO purposes
	 */
	@Deprecated
	public NotAuthorizedResult() {
	}

	public NotAuthorizedResult(String userName, String operationMessageCode) {
		this.userName = userName;
		this.operationMessageCode = operationMessageCode;

		messageCode = NotAuthorizedResult.SECURITY_AUTHORIZATION_MISSING;
	}

	public NotAuthorizedResult(String userName, String operationMessageCode, String subjectTitle) {
		Preconditions.checkArgument(StringUtils.hasText(subjectTitle), "Subject title must be provided");
		this.userName = userName;
		this.operationMessageCode = operationMessageCode;
		this.subjectTitle = subjectTitle;

		messageCode = NotAuthorizedResult.SECURITY_AUTHORIZATION_MISSING_ON_SUBJECT;
	}

	@Override
	public String getMessageCode() {
		return messageCode;
	}

	@Override
	public MessageArgConverter[] getMessageArgsConverters() {
		return messageArgsConverters;
	}

	@Override
	public Object[] getMessageArgs() {
		return new Object[] { userName, operationMessageCode, subjectTitle };
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOperationMessageCode() {
		return operationMessageCode;
	}

	public void setOperationMessageCode(String operationMessageCode) {
		this.operationMessageCode = operationMessageCode;
	}

	public String getSubjectTitle() {
		return subjectTitle;
	}

	public void setSubjectTitle(String subjectTitle) {
		this.subjectTitle = subjectTitle;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

}
