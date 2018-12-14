package org.summerb.approaches.validation;

import java.io.Serializable;
import java.util.Arrays;

import org.summerb.approaches.i18n.HasMessageArgs;
import org.summerb.approaches.i18n.HasMessageCode;

/**
 * This class describes validation error. It could be subclassed to provide more
 * details on error.
 * 
 * General idea is to not use here localized messages, only codes and typed
 * subclasses if needed
 * 
 * @author sergey.karpushin
 * 
 *         TBD: Make this class abstract
 */
public class ValidationError implements Serializable, HasMessageCode, HasMessageArgs {
	private static final long serialVersionUID = 2414529436328740490L;

	/**
	 * Field token. Actually name of object field
	 */
	private String fieldToken;

	/**
	 * Message about this field
	 */
	private String messageCode;

	/**
	 * Message messageArgs
	 */
	private Object[] messageArgs;

	/**
	 * @deprecated used only for serialization
	 */
	@Deprecated
	public ValidationError() {
	}

	public ValidationError(String userMessageCode, String fieldToken) {
		if (userMessageCode == null || fieldToken == null) {
			throw new IllegalArgumentException("Message and field name token cannot be null.");
		}
		this.messageCode = userMessageCode;
		this.fieldToken = fieldToken;
	}

	public ValidationError(String userMessageCode, String fieldToken, Object... args) {
		this(userMessageCode, fieldToken);
		this.messageArgs = args;
	}

	@Override
	public String getMessageCode() {
		return messageCode;
	}

	public String getFieldToken() {
		return fieldToken;
	}

	public void setMessageCode(String userMessage) {
		this.messageCode = userMessage;
	}

	public void setFieldToken(String fieldToken) {
		this.fieldToken = fieldToken;
	}

	@Override
	public Object[] getMessageArgs() {
		return messageArgs;
	}

	@Override
	public String toString() {
		return "" + getClass().getSimpleName() + " (field = '" + getFieldToken() + "', msgCode = '" + getMessageCode()
				+ "', msgArgs = '" + Arrays.toString(getMessageArgs()) + "')";
	}

}
