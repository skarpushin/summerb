package org.summerb.approaches.jdbccrud.api.query;

import java.io.Serializable;

/**
 * @author sergey.karpushin
 */
public interface Restriction<T> extends Serializable {
	boolean isMeet(T subjectValue);

	@Override
	boolean equals(Object obj);

	@Override
	int hashCode();
}
