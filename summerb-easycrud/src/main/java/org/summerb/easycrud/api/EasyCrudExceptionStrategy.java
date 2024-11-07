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
package org.summerb.easycrud.api;

import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.summerb.easycrud.api.exceptions.EntityNotFoundException;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.impl.EasyCrudExceptionStrategyDefaultImpl;

/**
 * Exception handling strategy. Rarely but you might need to override how exceptions are handled
 * during {@link EasyCrudService} operations.
 *
 * <p>Default implementation {@link EasyCrudExceptionStrategyDefaultImpl} supposed to be enough in
 * most cases
 *
 * @author sergey.karpushin
 */
public interface EasyCrudExceptionStrategy<TId, TRow extends HasId<TId>> {

  EntityNotFoundException buildNotFoundException(String subjectTypeMessageCode, TId identity);

  RuntimeException handleExceptionAtCreate(Throwable t, TRow row);

  RuntimeException handleExceptionAtDelete(Throwable t, TId id, TRow rowOptional);

  RuntimeException handleAffectedIncorrectNumberOfRowsOnDelete(
      JdbcUpdateAffectedIncorrectNumberOfRowsException t, TRow rowOptional);

  RuntimeException handleExceptionAtUpdate(Throwable t, TRow row);

  RuntimeException handleAffectedIncorrectNumberOfRowsOnUpdate(
      JdbcUpdateAffectedIncorrectNumberOfRowsException t, TRow rowOptional);

  RuntimeException handleExceptionAtFind(Throwable t);

  RuntimeException handleExceptionAtDeleteByQuery(Throwable t);

}
