/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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
package org.summerb.easycrud.exceptions;

import com.google.common.base.Preconditions;
import java.sql.SQLIntegrityConstraintViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.EasyCrudMessageCodes;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.row.HasTimestamps;
import org.summerb.easycrud.validation.errors.ReferencedRowCannotBeDeletedValidationError;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.utils.exceptions.ExceptionUtils;
import org.summerb.validation.ValidationException;

/**
 * @author sergey.karpushin
 */
public class EasyCrudExceptionStrategyDefaultImpl<
        TId extends Comparable<TId>, TRow extends HasId<TId>>
    implements EasyCrudExceptionStrategy<TId, TRow> {
  protected String rowMessageCode;

  public EasyCrudExceptionStrategyDefaultImpl(String rowMessageCode) {
    Preconditions.checkArgument(StringUtils.hasText(rowMessageCode));
    this.rowMessageCode = rowMessageCode;
  }

  @Override
  public EntityNotFoundException buildNotFoundException(TId identity) {
    return new EntityNotFoundException(rowMessageCode, identity);
  }

  @Override
  public RuntimeException exceptionAtCreate(Throwable t, TRow row) {
    if (t instanceof NotAuthorizedException nae) {
      return nae;
    }
    if (t instanceof ValidationException vexc) {
      return vexc;
    }

    return new EasyCrudUnexpectedException(
        EasyCrudMessageCodes.UNEXPECTED_FAILED_TO_CREATE, rowMessageCode, t);
  }

  @Override
  public RuntimeException exceptionAtDelete(Throwable t, TId id, TRow rowOptional)
      throws NotAuthorizedException, EntityNotFoundException {

    if (t instanceof NotAuthorizedException nae) {
      return nae;
    }
    if (t instanceof EntityNotFoundException enfe) {
      return enfe;
    }

    ValidationException fve = tryExtractConstraintViolation(t);
    if (fve != null) {
      return new RuntimeException("Constraint violation", fve);
    }

    return new EasyCrudUnexpectedException(
        EasyCrudMessageCodes.UNEXPECTED_FAILED_TO_DELETE, rowMessageCode, t);
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
  public RuntimeException exceptionAtDeleteByQuery(Throwable t, Query<TId, TRow> query)
      throws NotAuthorizedException {

    if (t instanceof NotAuthorizedException nae) {
      return nae;
    }
    if (t instanceof EntityNotFoundException enfe) {
      return enfe;
    }

    return new EasyCrudUnexpectedException(
        EasyCrudMessageCodes.UNEXPECTED_FAILED_TO_DELETE, rowMessageCode, t);
  }

  @Override
  public RuntimeException affectedIncorrectNumberOfRowsOnDelete(
      JdbcUpdateAffectedIncorrectNumberOfRowsException t, TRow rowOptional) {
    return new OptimisticLockingFailureException(
        "Optimistic lock failed, record was already concurrently updated", t);
  }

  @Override
  public RuntimeException affectedIncorrectNumberOfRowsOnUpdate(
      JdbcUpdateAffectedIncorrectNumberOfRowsException t, TRow rowOptional) {
    if (rowOptional instanceof HasTimestamps) {
      return new OptimisticLockingFailureException(
          "Optimistic lock failed, record was already concurrently updated", t);
    }
    return t;
  }

  @Override
  public RuntimeException exceptionAtUpdate(Throwable t, TRow row) {
    if (t instanceof NotAuthorizedException nae) {
      return nae;
    }
    if (t instanceof EntityNotFoundException enfe) {
      return enfe;
    }
    if (t instanceof ValidationException vexc) {
      return vexc;
    }

    return new EasyCrudUnexpectedException(
        EasyCrudMessageCodes.UNEXPECTED_FAILED_TO_UPDATE, rowMessageCode, t);
  }

  @Override
  public RuntimeException exceptionAtFind(Throwable t, Object criteria)
      throws NotAuthorizedException {
    if (t instanceof NotAuthorizedException nae) {
      return nae;
    }

    return new EasyCrudUnexpectedException(
        EasyCrudMessageCodes.UNEXPECTED_FAILED_TO_FIND, rowMessageCode, t);
  }
}
