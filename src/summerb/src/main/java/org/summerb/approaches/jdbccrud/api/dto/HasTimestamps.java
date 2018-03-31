package org.summerb.approaches.jdbccrud.api.dto;

import org.summerb.approaches.jdbccrud.api.EasyCrudService;

/**
 * Let your DTO impl this interface. {@link EasyCrudService} then will set
 * createdAt field upon creation and update updatedAt on update.
 * 
 * Also it will make it possible to easily use optimistic locking technique. See
 * {@link EasyCrudService#deleteByIdOptimistic(Object, long)}. Also
 * {@link EasyCrudService#update(Object)} will verify value of this field before
 * updating row. Only if value of modifiedAt matches row will be modified,
 * otherwise operation will be considered a failure.
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
