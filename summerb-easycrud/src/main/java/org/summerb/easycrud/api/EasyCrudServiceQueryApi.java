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
package org.summerb.easycrud.api;

import java.util.List;
import org.summerb.easycrud.api.dto.OrderBy;
import org.summerb.easycrud.api.exceptions.EntityNotFoundException;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;

/**
 * These service methods are declared in own interface (instead of {@link EasyCrudService}) to
 * decrease coupling between {@link org.summerb.easycrud.api.query.Query} and {@link
 * EasyCrudService}
 *
 * @param <TId> type of primary key
 * @param <TRow> type of Row
 */
public interface EasyCrudServiceQueryApi<TId, TRow extends HasId<TId>> {
  /**
   * @param query query for locating row
   * @return Row or null if not found. If more than 1 row matched query exception will be thrown
   * @throws NotAuthorizedException if user is not authorized to perform this operation
   */
  TRow findOneByQuery(Query<TId, TRow> query);

  /**
   * @param query query for locating row
   * @return a Row. If more (or less) than 1 row matched query exception will be thrown
   * @throws NotAuthorizedException if user is not authorized to perform this operation
   */
  TRow getOneByQuery(Query<TId, TRow> query);

  /**
   * @param query query for locating row
   * @param orderBy order by, optional - can be missing/null
   * @return row, never null. If nothing found, throws {@link EntityNotFoundException}
   * @throws NotAuthorizedException if user is not authorized to perform this operation
   * @throws EntityNotFoundException in case entity does not exist
   */
  TRow getFirstByQuery(Query<TId, TRow> query, OrderBy... orderBy);

  /**
   * @param query query for locating row
   * @param orderBy order by, optional - can be missing/null
   * @return row, or null if not found
   * @throws NotAuthorizedException if user is not authorized to perform this operation
   */
  TRow findFirstByQuery(Query<TId, TRow> query, OrderBy... orderBy);

  /**
   * @param pagerParams pagination parameters
   * @param optionalQuery optional Query, might be null
   * @param orderBy optional orderBy, might be missing/null
   * @return results, might be empty, but never null
   * @throws NotAuthorizedException if user is not authorized to perform this operation
   */
  PaginatedList<TRow> find(
      PagerParams pagerParams, Query<TId, TRow> optionalQuery, OrderBy... orderBy);

  /**
   * @param optionalQuery - optional {@link Query}. If null, then similar to findAll()
   * @param orderBy optional orderBy, might be missing/null
   * @return results, might be empty, but never null
   * @throws NotAuthorizedException if user is not authorized to perform this operation
   */
  List<TRow> findAll(Query<TId, TRow> optionalQuery, OrderBy... orderBy);

  /**
   * Same as findAll, but will throw {@link EntityNotFoundException} if nothing found
   *
   * @param optionalQuery optional query
   * @param orderBy optional order by
   * @return list of found items (at least one) or throws {@link EntityNotFoundException} in case
   *     nothing found
   */
  List<TRow> getAll(Query<TId, TRow> optionalQuery, OrderBy... orderBy);
}
