package org.summerb.approaches.jdbccrud.impl;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.util.StringUtils;
import org.summerb.approaches.jdbccrud.api.EasyCrudExceptionStrategy;
import org.summerb.approaches.jdbccrud.api.EasyCrudMessageCodes;
import org.summerb.approaches.jdbccrud.api.exceptions.EasyCrudUnexpectedException;
import org.summerb.approaches.jdbccrud.api.exceptions.EntityNotFoundException;
import org.summerb.approaches.jdbccrud.api.exceptions.GenericEntityNotFoundException;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

/**
 * 
 * @author sergey.karpushin
 *
 */
public class EasyCrudExceptionStrategyDefaultImpl<TId> implements EasyCrudExceptionStrategy<TId> {
	private String entityCode;

	public EasyCrudExceptionStrategyDefaultImpl(String entityTypeMessageCode) {
		Preconditions.checkArgument(StringUtils.hasText(entityTypeMessageCode));
		this.entityCode = entityTypeMessageCode;
	}

	@Override
	public RuntimeException handleExceptionAtCreate(Throwable t)
			throws FieldValidationException, NotAuthorizedException {
		Throwables.propagateIfInstanceOf(t, FieldValidationException.class);
		Throwables.propagateIfInstanceOf(t, NotAuthorizedException.class);

		return buildUnexpectedAtCreate(t);
	}

	protected RuntimeException buildUnexpectedAtCreate(Throwable t) throws FieldValidationException {
		return new EasyCrudUnexpectedException(EasyCrudMessageCodes.UNEXPECTED_FAILED_TO_CREATE, entityCode, t);
	}

	@Override
	public EntityNotFoundException buildNotFoundException(String subjectTypeMessageCode, TId identity) {
		return new GenericEntityNotFoundException(subjectTypeMessageCode, identity);
	}

	@Override
	public RuntimeException handleExceptionAtDelete(Throwable t)
			throws NotAuthorizedException, EntityNotFoundException {
		Throwables.propagateIfInstanceOf(t, NotAuthorizedException.class);
		Throwables.propagateIfInstanceOf(t, EntityNotFoundException.class);
		return buildUnexpectedAtDelete(t);
	}

	@Override
	public RuntimeException handleExceptionAtDeleteByQuery(Throwable t) throws NotAuthorizedException {
		Throwables.propagateIfInstanceOf(t, NotAuthorizedException.class);
		return buildUnexpectedAtDelete(t);
	}

	protected RuntimeException buildUnexpectedAtDelete(Throwable t) {
		return new EasyCrudUnexpectedException(EasyCrudMessageCodes.UNEXPECTED_FAILED_TO_DELETE, entityCode, t);
	}

	@Override
	public RuntimeException buildOptimisticLockException() {
		return new OptimisticLockingFailureException("No records were affected");
	}

	@Override
	public RuntimeException handleExceptionAtUpdate(Throwable t)
			throws FieldValidationException, NotAuthorizedException, EntityNotFoundException {
		Throwables.propagateIfInstanceOf(t, FieldValidationException.class);
		Throwables.propagateIfInstanceOf(t, NotAuthorizedException.class);
		Throwables.propagateIfInstanceOf(t, EntityNotFoundException.class);
		return buildUnexpectedAtUpdate(t);
	}

	protected RuntimeException buildUnexpectedAtUpdate(Throwable t) {
		return new EasyCrudUnexpectedException(EasyCrudMessageCodes.UNEXPECTED_FAILED_TO_UPDATE, entityCode, t);
	}

	@Override
	public RuntimeException handleExceptionAtFind(Throwable t) throws NotAuthorizedException {
		Throwables.propagateIfInstanceOf(t, NotAuthorizedException.class);
		return buildUnexpectedAtFind(t);
	}

	protected RuntimeException buildUnexpectedAtFind(Throwable t) {
		return new EasyCrudUnexpectedException(EasyCrudMessageCodes.UNEXPECTED_FAILED_TO_FIND, entityCode, t);
	}

}
