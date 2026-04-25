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

import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.summerb.easycrud.EasyCrudService;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;

/**
 * Exception translation strategy. Whenever exception happens during one of the operations served by
 * {@link EasyCrudService}, this strategy is used to translate them.
 *
 * <p>Default implementation {@link EasyCrudExceptionStrategyDefaultImpl} supposed to be enough in
 * most cases.
 *
 * <p>In case you need to override it, you might directly inject it into {@link EasyCrudService} if
 * you need to override it for specific case, or you can override {@link
 * EasyCrudExceptionStrategyFactory} if you want to override it for all cases.
 *
 * @author sergey.karpushin
 */
public interface EasyCrudExceptionStrategy<TId extends Comparable<TId>, TRow extends HasId<TId>> {

  EntityNotFoundException buildNotFoundException(TId identity);

  RuntimeException exceptionAtCreate(Throwable t, TRow row);

  RuntimeException exceptionAtDelete(Throwable t, TId id, TRow rowOptional);

  RuntimeException affectedIncorrectNumberOfRowsOnDelete(
      JdbcUpdateAffectedIncorrectNumberOfRowsException t, TRow rowOptional);

  RuntimeException exceptionAtUpdate(Throwable t, TRow row);

  RuntimeException affectedIncorrectNumberOfRowsOnUpdate(
      JdbcUpdateAffectedIncorrectNumberOfRowsException t, TRow rowOptional);

  /**
   * @param criteria the criteria used for this operation. Could be an ID (in case of regular
   *     getById invocation), could be SQL (in case of SqlQuery annotation usage), could be Query,
   *     JoinQuery, or even null in case no filtering criteria was provided
   */
  RuntimeException exceptionAtFind(Throwable t, Object criteria);

  RuntimeException exceptionAtDeleteByQuery(Throwable t, Query<TId, TRow> query);
}
