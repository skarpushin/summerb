package org.summerb.approaches.jdbccrud.api;

import org.summerb.approaches.jdbccrud.api.exceptions.EntityNotFoundException;
import org.summerb.approaches.jdbccrud.impl.EasyCrudExceptionStrategyDefaultImpl;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;

/**
 * Exception handlign strategy. Rarely but you might need to override how
 * exceptions are handled during {@link EasyCrudService} operations.
 * 
 * Default implementation {@link EasyCrudExceptionStrategyDefaultImpl} supposed
 * to be enough in most cases
 * 
 * @author sergey.karpushin
 *
 */
public interface EasyCrudExceptionStrategy<TId> {

	RuntimeException handleExceptionAtCreate(Throwable t) throws FieldValidationException, NotAuthorizedException;

	EntityNotFoundException buildNotFoundException(String subjectTypeMessageCode, TId identity);

	RuntimeException handleExceptionAtDelete(Throwable t) throws NotAuthorizedException, EntityNotFoundException;

	RuntimeException buildOptimisticLockException();

	RuntimeException handleExceptionAtUpdate(Throwable t)
			throws FieldValidationException, NotAuthorizedException, EntityNotFoundException;

	RuntimeException handleExceptionAtFind(Throwable t) throws NotAuthorizedException;

	RuntimeException handleExceptionAtDeleteByQuery(Throwable t) throws NotAuthorizedException;

}
