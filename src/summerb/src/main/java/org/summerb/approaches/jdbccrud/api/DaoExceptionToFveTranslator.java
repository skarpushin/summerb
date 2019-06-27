package org.summerb.approaches.jdbccrud.api;

import org.summerb.approaches.validation.FieldValidationException;

/**
 * Strategy to translate known DAO-level exception into
 * {@link FieldValidationException} upon create and update operations
 * 
 * NOTE: Name was shortened from
 * DaoExceptionToFieldValidationExceptionTranslator, which seems to be quite
 * long.
 * 
 * @author sergeyk
 *
 */
public interface DaoExceptionToFveTranslator {

	/**
	 * This method meant to be called from catch clause. If exception cannot be
	 * handled by this impl it should just do nothing. Otherwise it should throw
	 * {@link FieldValidationException}
	 * 
	 * @param t
	 *            exception
	 * 
	 * @throws FieldValidationException
	 *             translated exception, based on information in parameter t
	 */
	void translateAndThtowIfApplicable(Throwable t) throws FieldValidationException;

}
