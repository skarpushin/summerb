/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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

import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.EasyCrudExceptionStrategy;
import org.summerb.easycrud.api.EasyCrudMessageCodes;
import org.summerb.easycrud.api.exceptions.EasyCrudUnexpectedException;
import org.summerb.easycrud.api.exceptions.EntityNotFoundException;
import org.summerb.easycrud.api.exceptions.GenericEntityNotFoundException;
import org.summerb.easycrud.api.validation_errors.ReferencedRowCannotBeDeletedValidationError;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.utils.exceptions.ExceptionUtils;
import org.summerb.validation.ValidationException;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

/** @author sergey.karpushin */
public class EasyCrudExceptionStrategyDefaultImpl<TId> implements EasyCrudExceptionStrategy<TId> {
  protected String entityCode;

  public EasyCrudExceptionStrategyDefaultImpl(String entityTypeMessageCode) {
    Preconditions.checkArgument(StringUtils.hasText(entityTypeMessageCode));
    this.entityCode = entityTypeMessageCode;
  }

  @Override
  public RuntimeException handleExceptionAtCreate(Throwable t) {
    Throwables.throwIfInstanceOf(t, ValidationException.class);
    Throwables.throwIfInstanceOf(t, NotAuthorizedException.class);

    return buildUnexpectedAtCreate(t);
  }

  protected RuntimeException buildUnexpectedAtCreate(Throwable t) {
    return new EasyCrudUnexpectedException(
        EasyCrudMessageCodes.UNEXPECTED_FAILED_TO_CREATE, entityCode, t);
  }

  @Override
  public EntityNotFoundException buildNotFoundException(
      String subjectTypeMessageCode, TId identity) {
    return new GenericEntityNotFoundException(subjectTypeMessageCode, identity);
  }

  @Override
  public RuntimeException handleExceptionAtDelete(Throwable t)
      throws NotAuthorizedException, EntityNotFoundException {
    Throwables.throwIfInstanceOf(t, NotAuthorizedException.class);
    Throwables.throwIfInstanceOf(t, EntityNotFoundException.class);

    ValidationException fve = tryExtractConstraintViolation(t);
    if (fve != null) {
      return new RuntimeException("Constraint violation", fve);
    }

    return buildUnexpectedAtDelete(t);
  }

  /**
   * This is special case to handle errors like: "Cannot delete or update a parent row: a foreign
   * key constraint fails (`cdb`.`badges`, CONSTRAINT `badges_FK_care_team_members` FOREIGN KEY
   * (`care_team_member_id`) REFERENCES `care_team_members` (`id`))"
   *
   * @param t throwable that potentially might contain such information
   * @return {@link ValidationException} if such error found, or null otherwise
   */
  protected ValidationException tryExtractConstraintViolation(Throwable t) {
    SQLIntegrityConstraintViolationException sqlExc =
        ExceptionUtils.findExceptionOfType(t, SQLIntegrityConstraintViolationException.class);
    if (sqlExc == null) {
      return null;
    }

    if (sqlExc.getErrorCode() == 1451 && "23000".equals(sqlExc.getSQLState())) {
      return new ValidationException(new ReferencedRowCannotBeDeletedValidationError());
    }

    return null;
  }

  @Override
  public RuntimeException handleExceptionAtDeleteByQuery(Throwable t)
      throws NotAuthorizedException {
    Throwables.throwIfInstanceOf(t, NotAuthorizedException.class);
    return buildUnexpectedAtDelete(t);
  }

  protected RuntimeException buildUnexpectedAtDelete(Throwable t) {
    return new EasyCrudUnexpectedException(
        EasyCrudMessageCodes.UNEXPECTED_FAILED_TO_DELETE, entityCode, t);
  }

  @Override
  public RuntimeException buildOptimisticLockException() {
    return new OptimisticLockingFailureException("Optimistic lock failed, record was already updated but someone else");
  }

  @Override
  public RuntimeException handleExceptionAtUpdate(Throwable t) {
    Throwables.throwIfInstanceOf(t, ValidationException.class);
    Throwables.throwIfInstanceOf(t, NotAuthorizedException.class);
    Throwables.throwIfInstanceOf(t, EntityNotFoundException.class);
    return buildUnexpectedAtUpdate(t);
  }

  protected RuntimeException buildUnexpectedAtUpdate(Throwable t) {
    return new EasyCrudUnexpectedException(
        EasyCrudMessageCodes.UNEXPECTED_FAILED_TO_UPDATE, entityCode, t);
  }

  @Override
  public RuntimeException handleExceptionAtFind(Throwable t) throws NotAuthorizedException {
    Throwables.throwIfInstanceOf(t, NotAuthorizedException.class);
    return buildUnexpectedAtFind(t);
  }

  protected RuntimeException buildUnexpectedAtFind(Throwable t) {
    return new EasyCrudUnexpectedException(
        EasyCrudMessageCodes.UNEXPECTED_FAILED_TO_FIND, entityCode, t);
  }
}
