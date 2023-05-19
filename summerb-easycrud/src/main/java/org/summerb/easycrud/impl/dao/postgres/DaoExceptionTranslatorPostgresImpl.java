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
package org.summerb.easycrud.impl.dao.postgres;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.DaoExceptionTranslatorAbstract;
import org.summerb.utils.exceptions.ExceptionUtils;
import org.summerb.validation.ValidationContext;
import org.summerb.validation.ValidationException;
import org.summerb.validation.errors.DuplicateRecord;

/**
 * Postgres-specific impl
 *
 * @author sergeyk
 */
public class DaoExceptionTranslatorPostgresImpl extends DaoExceptionTranslatorAbstract {
  @Override
  public void translateAndThrowIfApplicable(Throwable t){
    throwIfDuplicate(t);

    /**
     * TODO: We should also be able to translate "data too long" exception. See
     * DaoExceptionUtils#findTruncatedFieldNameIfAny
     */
  }

  protected void throwIfDuplicate(Throwable t){
    DuplicateKeyException dke = ExceptionUtils.findExceptionOfType(t, DuplicateKeyException.class);
    if (dke == null) {
      return;
    }

    PSQLException dkep = ExceptionUtils.findExceptionOfType(t, PSQLException.class);
    if (dkep == null) {
      return;
    }

    String detail = dkep.getServerErrorMessage().getDetail();
    if (!StringUtils.hasText(detail)) {
      return;
    }

    if (!detail.startsWith("Key (")) {
      return;
    }

    String fieldsStr = detail.substring(5, detail.indexOf(")"));
    String[] fields = fieldsStr.split(",");

    ValidationContext<?> ctx = new ValidationContext<>();
    for (String field : fields) {
      ctx.add(new DuplicateRecord(JdbcUtils.convertUnderscoreNameToPropertyName(field.trim())));
    }
    ctx.throwIfHasErrors();
  }
}
