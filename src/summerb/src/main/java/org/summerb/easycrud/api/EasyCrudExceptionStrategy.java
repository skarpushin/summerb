package org.summerb.easycrud.api;

import org.summerb.easycrud.api.exceptions.EntityNotFoundException;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.FieldValidationException;

/**
 * 
 * @author sergey.karpushin
 *
 */public interface EasyCrudExceptionStrategy<TId> {

	RuntimeException handleExceptionAtCreate(Throwable t) throws FieldValidationException, NotAuthorizedException;

	EntityNotFoundException buildNotFoundException(String subjectTypeMessageCode, TId identity);

	RuntimeException handleExceptionAtDelete(Throwable t) throws NotAuthorizedException, EntityNotFoundException;

	RuntimeException buildOptimisticLockException();

	RuntimeException handleExceptionAtUpdate(Throwable t) throws FieldValidationException, NotAuthorizedException,
			EntityNotFoundException;

	RuntimeException handleExceptionAtFind(Throwable t) throws NotAuthorizedException;

	RuntimeException handleExceptionAtDeleteByQuery(Throwable t) throws NotAuthorizedException;

}
