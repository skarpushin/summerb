package org.summerb.approaches.jdbccrud.api.query;

/**
 * 
 * @author sergey.karpushin
 *
 */
public interface Restriction<T> {
	boolean isMeet(T subjectValue);

	@Override
	boolean equals(Object obj);

	@Override
	int hashCode();
}
