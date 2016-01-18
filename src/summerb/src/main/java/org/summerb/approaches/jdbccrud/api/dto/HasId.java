package org.summerb.approaches.jdbccrud.api.dto;

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
