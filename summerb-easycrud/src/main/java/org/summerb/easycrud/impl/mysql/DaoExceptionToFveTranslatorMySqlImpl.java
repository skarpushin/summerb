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
package org.summerb.easycrud.impl.mysql;

import java.sql.DataTruncation;
import java.sql.SQLSyntaxErrorException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.support.JdbcUtils;
import org.summerb.easycrud.api.DaoExceptionTranslatorAbstract;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.common.ServiceDataTruncationException;
import org.summerb.utils.exceptions.ExceptionUtils;
import org.summerb.validation.ValidationException;
import org.summerb.validation.errors.DuplicateRecord;

/**
 * MySQL-specific impl
 *
 * @author sergeyk
 */
public class DaoExceptionToFveTranslatorMySqlImpl extends DaoExceptionTranslatorAbstract {
  public static final String MYSQL_CONSTRAINT_PRIMARY = "PRIMARY";
  public static final String MYSQL_CONSTRAINT_UNIQUE = "_UNIQUE";

  @Override
  public void translateAndThrowIfApplicable(Throwable t) throws ValidationException {
    throwIfDuplicate(t);
    throwIfTruncationError(t);
  }

  protected void throwIfDuplicate(Throwable t) throws ValidationException {
    DuplicateKeyException dke = ExceptionUtils.findExceptionOfType(t, DuplicateKeyException.class);
    if (dke == null) {
      return;
    }

    String constraint = findViolatedConstraintName(dke);
    if (constraint == null) {
      // NOTE: This means that this handler is not capable of parsing such error. We
      // should log it
      return;
    }

    // Handle case when uuid is duplicated
    if (DaoExceptionToFveTranslatorMySqlImpl.MYSQL_CONSTRAINT_PRIMARY.equals(constraint)) {
      throw new ValidationException(new DuplicateRecord(HasId.FN_ID));
    }

    if (!constraint.contains(DaoExceptionToFveTranslatorMySqlImpl.MYSQL_CONSTRAINT_UNIQUE)) {
      throw new IllegalArgumentException("Constraint violation " + constraint, dke);
    }

    String fieldName =
        constraint.substring(
            0, constraint.indexOf(DaoExceptionToFveTranslatorMySqlImpl.MYSQL_CONSTRAINT_UNIQUE));
    if (fieldName.contains("_")) {
      fieldName = JdbcUtils.convertUnderscoreNameToPropertyName(fieldName);
    }

    throw new ValidationException(new DuplicateRecord(fieldName));
  }

  /**
   * Parse exception and find constraint name.
   *
   * <p>Current impl will parse text string which format is expected to be like: "Duplicate entry
   * 'sameUuid' for key 'PRIMARY'"
   *
   * @param duplicateKeyException exception received from DAO
   * @return null if failed to parse, constraint name otherwise
   */
  public static String findViolatedConstraintName(DuplicateKeyException duplicateKeyException) {
    if (duplicateKeyException == null) {
      return null;
    }

    String message = duplicateKeyException.getMessage();
    int pos = message.indexOf("for key");
    if (pos < 0) {
      return null;
    }

    message = message.substring(pos);
    if (message.indexOf("'") < 0) {
      return null;
    }

    String[] parts = message.split("'");
    if (parts.length < 2) {
      return null;
    }

    return parts[1];
  }

  /**
   * Will detect truncation error and substitute value field name with property name
   *
   * @param t
   * @param propertyName
   */
  private void throwIfTruncationError(Throwable t) {
    Exception exc = ExceptionUtils.findExceptionOfType(t, DataTruncation.class);
    if (exc == null) {
      exc = ExceptionUtils.findExceptionOfType(t, SQLSyntaxErrorException.class);
      if (exc == null) {
        return;
      }
    }

    String fieldName = findTruncatedFieldNameIfAny(exc.getMessage());

    // throw new ValidationException(new
    // LengthMustBeLessOrEqual(currentSize?, allowedSize?, fieldName));

    // NOTE: We're throwing ServiceDataTruncationException instead of
    // ValidationException because it feels right (there is commonly known
    // exception DataTruncation) and because we do not know currentSize and
    // allowedSize here
    throw ServiceDataTruncationException.envelopeFor(
        JdbcUtils.convertUnderscoreNameToPropertyName(fieldName), t);
  }

  /**
   * Find a name of the field which was truncated
   *
   * @param t exception received from Spring JDBC
   * @return field name if any, otherwize null
   */
  private String findTruncatedFieldNameIfAny(String msg) {
    if (!msg.contains("too long")) {
      return null;
    }

    String[] params = msg.split("\'");
    if (params.length < 2) {
      return null;
    }

    String fieldName = params[1];

    // Ok now convert it to camel case if needed
    if (fieldName.contains("_")) {
      fieldName = JdbcUtils.convertUnderscoreNameToPropertyName(fieldName);
    }

    return fieldName;
  }
}
