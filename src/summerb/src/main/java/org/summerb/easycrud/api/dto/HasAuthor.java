package org.summerb.easycrud.api.dto;

/**
 * 
 * @author sergey.karpushin
 *
 */
public interface HasAuthor {
	public static final String FN_CREATED_BY = "createdBy";
	public static final String FN_MODIFIED_BY = "modifiedBy";

	void setCreatedBy(String userUuid);

	String getCreatedBy();

	void setModifiedBy(String userUuid);

	String getModifiedBy();
}
