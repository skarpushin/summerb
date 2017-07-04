package org.summerb.approaches.security.api.exceptions;

import org.summerb.approaches.i18n.HasMessageArgs;
import org.summerb.approaches.i18n.HasMessageArgsConverters;
import org.summerb.approaches.i18n.HasMessageCode;
import org.summerb.approaches.i18n.MessageArgConverter;
import org.summerb.approaches.security.api.dto.NotAuthorizedResult;
import org.summerb.utils.exceptions.dto.HasErrorDescriptionObject;

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
