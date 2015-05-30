package org.summerb.easycrud.api.exceptions;

import org.summerb.easycrud.api.EasyCrudMessageCodes;
import org.summerb.i18n.HasMessageArgsConverters;
import org.summerb.i18n.MessageArgConverter;
import org.summerb.i18n.MessageCodeMessageArgConverter;

/**
 * 
 * @author sergey.karpushin
 *
 */public class GenericEntityNotFoundException extends EntityNotFoundException implements HasMessageArgsConverters {
	private static final long serialVersionUID = -953061537781444391L;

	private String subjectTypeMessageCode;

	public GenericEntityNotFoundException(String subjectTypeMessageCode, Object identity) {
		this(subjectTypeMessageCode, identity, null);
	}

	public GenericEntityNotFoundException(String subjectTypeMessageCode, Object identity, Throwable cause) {
		super("Entity " + subjectTypeMessageCode + " identified by '" + identity + "' not found", identity, cause);
		this.setSubjectTypeMessageCode(subjectTypeMessageCode);
	}

	@Override
	public Object[] getMessageArgs() {
		return new Object[] { getSubjectTypeMessageCode(), getIdentity() };
	}

	@Override
	public String getMessageCode() {
		return EasyCrudMessageCodes.ENTITY_NOT_FOUND;
	}

	@Override
	public MessageArgConverter[] getMessageArgsConverters() {
		return new MessageArgConverter[] { MessageCodeMessageArgConverter.INSTANCE, null };
	}

	public String getSubjectTypeMessageCode() {
		return subjectTypeMessageCode;
	}

	public void setSubjectTypeMessageCode(String subjectTypeMessageCode) {
		this.subjectTypeMessageCode = subjectTypeMessageCode;
	}

}
