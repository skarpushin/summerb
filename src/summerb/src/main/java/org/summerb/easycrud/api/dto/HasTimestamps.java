package org.summerb.easycrud.api.dto;

/**
 * 
 * @author sergey.karpushin
 *
 */public interface HasTimestamps {
	public static final String FN_CREATED_AT = "createdAt";
	public static final String FN_MODIFIED_AT = "modifiedAt";

	void setCreatedAt(long createdAt);

	long getCreatedAt();

	void setModifiedAt(long modifiedAt);

	long getModifiedAt();
}
