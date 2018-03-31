package org.summerb.approaches.jdbccrud.api.dto;

import org.summerb.approaches.jdbccrud.api.EasyCrudService;

/**
 * Impl this interface with your DTO to make {@link EasyCrudService} track users
 * who created and updated these rows.
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
