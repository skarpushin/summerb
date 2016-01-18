package org.summerb.approaches.jdbccrud.api;

import org.summerb.approaches.validation.FieldValidationException;

/**
 * 
 * @author sergey.karpushin
 *
 */
public interface EasyCrudValidationStrategy<TDto> {
	void validateForCreate(TDto dto) throws FieldValidationException;

	void validateForUpdate(TDto existingVersion, TDto newVersion) throws FieldValidationException;
}
