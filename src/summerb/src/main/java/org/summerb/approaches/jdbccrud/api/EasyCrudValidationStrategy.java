package org.summerb.approaches.jdbccrud.api;

import org.summerb.approaches.jdbccrud.impl.EasyCrudServicePluggableImpl;
import org.summerb.approaches.jdbccrud.impl.EasyCrudValidationStrategyAbstract;
import org.summerb.approaches.validation.FieldValidationException;

/**
 * Strategy interface that has method for validating DTO before it will be
 * created and updated.
 * 
 * <p>
 * 
 * Normally will be injected into {@link EasyCrudService}. In case of using
 * {@link EasyCrudServicePluggableImpl} via {@link EasyCrudWireTap}, but also
 * impl of this interface can be used separately
 * 
 * <p>
 * 
 * In case validation rules are the same for both
 * {@link EasyCrudService#create(Object)} and
 * {@link EasyCrudService#update(Object)}, consider sub-classing
 * {@link EasyCrudValidationStrategyAbstract}
 * 
 * @author sergey.karpushin
 *
 */
public interface EasyCrudValidationStrategy<TDto> {
	void validateForCreate(TDto dto) throws FieldValidationException;

	void validateForUpdate(TDto existingVersion, TDto newVersion) throws FieldValidationException;
}
