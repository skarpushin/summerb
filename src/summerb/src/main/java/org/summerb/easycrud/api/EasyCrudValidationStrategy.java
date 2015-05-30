package org.summerb.easycrud.api;

import org.summerb.validation.FieldValidationException;

/**
 * 
 * @author sergey.karpushin
 *
 */public interface EasyCrudValidationStrategy<TDto> {
	void validateForCreate(TDto dto) throws FieldValidationException;

	void validateForUpdate(TDto existingVersion, TDto newVersion) throws FieldValidationException;
}
