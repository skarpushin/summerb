/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.easycrud.impl;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.EasyCrudExceptionStrategy;
import org.summerb.easycrud.api.EasyCrudMessageCodes;
import org.summerb.easycrud.api.exceptions.EasyCrudUnexpectedException;
import org.summerb.easycrud.api.exceptions.EntityNotFoundException;
import org.summerb.easycrud.api.exceptions.GenericEntityNotFoundException;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.FieldValidationException;

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
		Throwables.throwIfInstanceOf(t, FieldValidationException.class);
		Throwables.throwIfInstanceOf(t, NotAuthorizedException.class);

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
		Throwables.throwIfInstanceOf(t, NotAuthorizedException.class);
		Throwables.throwIfInstanceOf(t, EntityNotFoundException.class);
		return buildUnexpectedAtDelete(t);
	}

	@Override
	public RuntimeException handleExceptionAtDeleteByQuery(Throwable t) throws NotAuthorizedException {
		Throwables.throwIfInstanceOf(t, NotAuthorizedException.class);
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
		Throwables.throwIfInstanceOf(t, FieldValidationException.class);
		Throwables.throwIfInstanceOf(t, NotAuthorizedException.class);
		Throwables.throwIfInstanceOf(t, EntityNotFoundException.class);
		return buildUnexpectedAtUpdate(t);
	}

	protected RuntimeException buildUnexpectedAtUpdate(Throwable t) {
		return new EasyCrudUnexpectedException(EasyCrudMessageCodes.UNEXPECTED_FAILED_TO_UPDATE, entityCode, t);
	}

	@Override
	public RuntimeException handleExceptionAtFind(Throwable t) throws NotAuthorizedException {
		Throwables.throwIfInstanceOf(t, NotAuthorizedException.class);
		return buildUnexpectedAtFind(t);
	}

	protected RuntimeException buildUnexpectedAtFind(Throwable t) {
		return new EasyCrudUnexpectedException(EasyCrudMessageCodes.UNEXPECTED_FAILED_TO_FIND, entityCode, t);
	}

}
