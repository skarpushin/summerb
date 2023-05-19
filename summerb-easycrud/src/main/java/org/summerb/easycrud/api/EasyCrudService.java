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

import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.easycrud.api.exceptions.EntityNotFoundException;
import org.summerb.easycrud.api.query.OrderBy;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.api.row.HasTimestamps;
import org.summerb.i18n.HasMessageCode;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.ValidationException;

/**
 * This service is intended for use with relatively simple Rows (DTO's). It implements simple
 * create-read-update-delete operations. Subclasses are welcome to extend this functionality if
 * needed as application evolves.
 *
 * <p>This service is perfect for simple Rows's like dictionaries. But it also can be used for
 * business logic.
 *
 * <p>EasyCrudService is Row-centric, it's good at working with 1 Row type only, which is mapped to
 * the table in the database. Each Row type requires it's own EasyCrudService.
 *
 * <p>It's not an ORM framework, so if you need to have a reference to a user, you'll create field
 * like "long userId", but not "User user".
 *
 * @author sergey.karpushin
 * @param <TId> type of primary key
 * @param <TRow> type of row
 */
public interface EasyCrudService<TId, TRow extends HasId<TId>> {

  /**
   * Create row
   *
   * @param row row to create
   * @return newly created row. It is possible that fields will differ due to ID set and other
   *     modifications set by {@link EasyCrudWireTap} injected into this service
   * @throws ValidationException in case of field validation errors. Some data access exceptions
   *     might be translated into field validatnion errors as well
   * @throws NotAuthorizedException if user is not authorized o perform this operation
   */
  @Transactional(rollbackFor = Throwable.class)
  TRow create(TRow row);

  /**
   * Update row
   *
   * @param row row to update
   * @return updated row. It is possible that fields will differ due to ID set and other
   *     modifications set by {@link EasyCrudWireTap} injected into this service
   * @throws ValidationException in case of field validation errors. Some data access exceptions
   *     might be translated into field validatnion errors as well
   * @throws NotAuthorizedException if user is not authorized o perform this operation
   * @throws EntityNotFoundException in case entity does not exist
   */
  @Transactional(rollbackFor = Throwable.class)
  TRow update(TRow row);

  /**
   * @param id id of row to find
   * @return row or null if not found
   * @throws NotAuthorizedException if user is not authorized o perform this operation
   */
  TRow findById(TId id);

  /**
   * @param id id of row to get
   * @return row, never null. If row not found, throws {@link EntityNotFoundException}
   * @throws NotAuthorizedException if user is not authorized o perform this operation
   * @throws EntityNotFoundException in case entity does not exist
   */
  TRow getById(TId id);

  /**
   * @param query query for locating row
   * @return Row or null if not found. If more than 1 row matched query exception will be thrown
   * @throws NotAuthorizedException if user is not authorized o perform this operation
   */
  TRow findOneByQuery(Query query);

  /**
   * @param query query for locating row
   * @param orderBy order by, optional - can be missing/null
   * @return row, never null. If nothing found, throws {@link EntityNotFoundException}
   * @throws NotAuthorizedException if user is not authorized o perform this operation
   * @throws EntityNotFoundException in case entity does not exist
   */
  TRow getFirstByQuery(Query query, OrderBy... orderBy);

  /**
   * @param query query for locating row
   * @param orderBy order by, optional - can be missing/null
   * @return row, or null if not found
   * @throws NotAuthorizedException if user is not authorized o perform this operation
   */
  TRow findFirstByQuery(Query query, OrderBy... orderBy);

  /**
   * @param pagerParams pagination parameters
   * @param optionalQuery optional Query, might be null
   * @param orderBy optional orderBy, might be missing/null
   * @return results, might be empty, but never null
   * @throws NotAuthorizedException if user is not authorized o perform this operation
   */
  PaginatedList<TRow> find(PagerParams pagerParams, Query optionalQuery, OrderBy... orderBy);

  /**
   * @param optionalQuery optional Query, might be null
   * @param orderBy optional orderBy, might be missing/null
   * @return results, might be empty, but never null
   * @param optionalQuery - optional {@link Query}. If null, then this call is same as {@link
   *     #findAll(OrderBy...)}
   * @throws NotAuthorizedException if user is not authorized o perform this operation
   */
  List<TRow> findAll(Query optionalQuery, OrderBy... orderBy);

  /**
   * @param orderBy optional orderBy, might be missing/null
   * @return results, might be empty, but never null
   * @throws NotAuthorizedException if user is not authorized o perform this operation
   */
  List<TRow> findAll(OrderBy... orderBy);

  /**
   * Deletes row by id.
   *
   * @param id id of the row to delete
   * @throws NotAuthorizedException if user is not authorized o perform this operation
   * @throws EntityNotFoundException in case entity does not exist
   */
  @Transactional(rollbackFor = Throwable.class)
  void deleteById(TId id);

  /**
   * Deletes row by example. If TRow implements {@link HasTimestamps} then this call is equivalent
   * to {@link #deleteByIdOptimistic(Object, long)}
   *
   * <p>Deletes row by example
   *
   * @param row row to get id and (optionally) modifiedAt from
   * @throws NotAuthorizedException if user is not authorized o perform this operation
   * @throws EntityNotFoundException in case entity does not exist
   */
  @Transactional(rollbackFor = Throwable.class)
  void delete(TRow row);

  /**
   * Delete row by id and modifiedAt. Applicable only in cases when row class extends {@link
   * HasTimestamps}.
   *
   * @param id id of the row to delete
   * @param modifiedAt timestamp of most recent modification of the row. Used for optimistic locking
   *     logic
   * @throws NotAuthorizedException if user is not authorized o perform this operation
   * @throws EntityNotFoundException in case entity does not exist
   * @throws OptimisticLockingFailureException if row version doesn't match provided one
   */
  @Transactional(rollbackFor = Throwable.class)
  void deleteByIdOptimistic(TId id, long modifiedAt);

  /**
   * Delete rows by query
   *
   * @param query query that used to locate rows for deletion
   * @return number of affected rows
   * @throws NotAuthorizedException if user is not authorized o perform this operation
   */
  @Transactional(rollbackFor = Throwable.class)
  int deleteByQuery(Query query);

  /** @return class of Row served by this service */
  Class<TRow> getRowClass();

  /**
   * @return entityTypeMessageCode. Same is used in exception messages codes {@link HasMessageCode}
   *     if thrown
   */
  String getRowMessageCode();
}
