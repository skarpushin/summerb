package org.summerb.utils;

/**
 * 
 * @author sergey.karpushin
 *
 * @param <T>
 */
public interface Clonnable<T> extends java.lang.Cloneable {
	T clone();
}
