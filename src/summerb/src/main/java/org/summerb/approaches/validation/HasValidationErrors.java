package org.summerb.approaches.validation;

import java.util.List;

/**
 * 
 * @author sergey.karpushin
 *
 */
public interface HasValidationErrors {
	List<? extends ValidationError> getValidationErrors();

	/**
	 * Handy method to check if there are any errors the same as ifcheck for the
	 * size of of collection
	 */
	boolean getHasErrors();
}
