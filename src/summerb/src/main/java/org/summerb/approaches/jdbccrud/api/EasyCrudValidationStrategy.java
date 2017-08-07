package org.summerb.approaches.jdbccrud.api;

import org.summerb.approaches.validation.FieldValidationException;

/**
 * Strategy interface that has method for validating DTO before creating and
 * update.
 * 
 * Normally will be injected into {@link EasyCrudService} via
 * {@link EasyCrudWireTap}, but also can be used separately
 * 
 * @author sergey.karpushin
 *
 */
public interface EasyCrudValidationStrategy<TDto> {
	void validateForCreate(TDto dto) throws FieldValidationException;

	void validateForUpdate(TDto existingVersion, TDto newVersion) throws FieldValidationException;
}
