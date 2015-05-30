package org.summerb.security.api.dto;

import java.io.Serializable;

import org.springframework.util.StringUtils;
import org.summerb.i18n.HasMessageArgs;
import org.summerb.i18n.HasMessageArgsConverters;
import org.summerb.i18n.HasMessageCode;
import org.summerb.i18n.MessageArgConverter;
import org.summerb.i18n.MessageCodeMessageArgConverter;

import com.google.common.base.Preconditions;

public class NotAuthorizedResult implements HasMessageCode, HasMessageArgs, HasMessageArgsConverters, Serializable {
	private static final transient long serialVersionUID = 1122164433294017483L;
	private static final transient MessageArgConverter[] messageArgsConverters = new MessageArgConverter[] { null,
			MessageCodeMessageArgConverter.INSTANCE, null };

	public static final String SECURITY_AUTHORIZATION_MISSING_ON_SUBJECT = "sec.missingPermissionOnSubject";
	public static final String SECURITY_AUTHORIZATION_MISSING = "sec.missingPermission";
	
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

		messageCode = SECURITY_AUTHORIZATION_MISSING;
	}

	public NotAuthorizedResult(String userName, String operationMessageCode, String subjectTitle) {
		Preconditions.checkArgument(StringUtils.hasText(subjectTitle), "Subject title must be provided");
		this.userName = userName;
		this.operationMessageCode = operationMessageCode;
		this.subjectTitle = subjectTitle;

		messageCode = SECURITY_AUTHORIZATION_MISSING_ON_SUBJECT;
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
