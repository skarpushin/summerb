package org.summerb.easycrud.api.query;

/**
 * 
 * @author sergey.karpushin
 *
 */
public interface Restriction<T> {
	boolean isMeet(T subjectValue);
}
