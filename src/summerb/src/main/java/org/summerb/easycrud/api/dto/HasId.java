package org.summerb.easycrud.api.dto;

/**
 * @author sergey.karpushin
 *
 * @param <TId>
 */
public interface HasId<TId> {
	public static final String FN_ID = "id";

	TId getId();

	void setId(TId id);
}
