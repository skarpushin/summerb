package org.summerb.approaches.jdbccrud.api.dto;

import org.summerb.approaches.jdbccrud.api.EasyCrudService;

/**
 * Let your DTO impl this interface. {@link EasyCrudService} then will set
 * createdAt field upon creation and update updatedAt on update. Also it will
 * make it possible to easily add optimistic locking.
 * 
 * @author sergey.karpushin
 *
 */
public interface HasTimestamps {
	public static final String FN_CREATED_AT = "createdAt";
	public static final String FN_MODIFIED_AT = "modifiedAt";

	void setCreatedAt(long createdAt);

	long getCreatedAt();

	void setModifiedAt(long modifiedAt);

	long getModifiedAt();
}
