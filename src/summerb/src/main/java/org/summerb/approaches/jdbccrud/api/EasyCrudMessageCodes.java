package org.summerb.approaches.jdbccrud.api;

/**
 * Common error codes for {@link EasyCrudService} operations
 * 
 * @author sergey.karpushin
 *
 */
public class EasyCrudMessageCodes {
	public static final String ENTITY_NOT_FOUND = "easycrud.entityNotFound";
	public static final String UNEXPECTED_FAILED_TO_CREATE = "easycrud.unexpecteFailed.create";
	public static final String UNEXPECTED_FAILED_TO_DELETE = "easycrud.unexpecteFailed.delete";
	public static final String UNEXPECTED_FAILED_TO_UPDATE = "easycrud.unexpecteFailed.update";
	public static final String UNEXPECTED_FAILED_TO_FIND = "easycrud.unexpecteFailed.find";
}
