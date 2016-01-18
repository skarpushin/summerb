package org.summerb.approaches.jdbccrud.api.exceptions;

import org.summerb.approaches.i18n.HasMessageArgs;
import org.summerb.approaches.i18n.HasMessageArgsConverters;
import org.summerb.approaches.i18n.HasMessageCode;
import org.summerb.approaches.i18n.MessageArgConverter;
import org.summerb.approaches.i18n.MessageCodeMessageArgConverter;

/**
 * 
 * @author sergey.karpushin
 *
 */
public class ConcurrentModificationException extends Exception
		implements HasMessageCode, HasMessageArgs, HasMessageArgsConverters {
	private static final long serialVersionUID = -8553908925129274626L;
	private static final MessageArgConverter[] MESSAGE_ARG_CONVERTERS = new MessageArgConverter[] {
			MessageCodeMessageArgConverter.INSTANCE, null };

	private String objectTypeName;
	private String objectIdentifier;

	/**
	 * @deprecated only for io
	 */
	@Deprecated
	public ConcurrentModificationException() {
	}

	public ConcurrentModificationException(String objectTypeName, String objectIdentifier) {
		this.objectTypeName = objectTypeName;
		this.objectIdentifier = objectIdentifier;
	}

	@Override
	public String getMessageCode() {
		return "exception.dao.concurrentModification";
	}

	@Override
	public Object[] getMessageArgs() {
		return new Object[] { objectTypeName, objectIdentifier };
	}

	@Override
	public MessageArgConverter[] getMessageArgsConverters() {
		return MESSAGE_ARG_CONVERTERS;
	}

	public String getObjectTypeName() {
		return objectTypeName;
	}

	public String getObjectIdentifier() {
		return objectIdentifier;
	}

}
