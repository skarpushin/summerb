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
package org.summerb.easycrud;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.summerb.easycrud.exceptions.EasyCrudExceptionStrategy;
import org.summerb.easycrud.exceptions.EntityNotFoundException;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.query.EasyCrudServiceQueryApi;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.OrderByBuilder;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.row.HasTimestamps;
import org.summerb.easycrud.tools.EasyCrudDtoUtils;
import org.summerb.easycrud.wireTaps.EasyCrudWireTap;
import org.summerb.i18n.HasMessageCode;
import org.summerb.methodCapturers.PropertyNameResolver;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.ValidationException;

/**
 * This service is intended to be used with relatively simple Rows (DTO's). It implements simple
 * create-read-update-delete operations. Subclasses are welcome to extend this functionality if
 * needed as the application evolves.
 *
 * <p>EasyCrudService is Row-centric, it's good at working with 1 Row type only, which is mapped to
 * the table in the database. Each Row type requires its own EasyCrudService.
 *
 * <p>It's not an ORM framework, so if you need to have a reference to a user, you'll create field
 * like <code>long userId</code>, but not <code>User user</code>.
 *
 * <p>Please refer to the <a
 * href="https://github.com/skarpushin/summerb/blob/master/summerb-easycrud/README.md">readme</a>
 * for extensive explanation on how to use it
 *
 * @author sergey.karpushin
 * @param <TId> type of primary key
 * @param <TRow> type of row
 */
public interface EasyCrudService<TId, TRow extends HasId<TId>>
    extends EasyCrudServiceQueryApi<TId, TRow> {

  /**
   * Build and get new actionable query. Use it to chain conditions and then call one of the methods
   * like get, find, etc...
   *
   * @return new instance of {@link Query}
   */
  Query<TId, TRow> query();

  /**
   * Same as {@link #query()}, plus allows assigning specific alias to this query field(s) (can be
   * useful when parsing OrderBy's and will appear in SQL statements when logging is turned on).
   *
   * <p>By default, the underlying table name concatenated with some integer will be used as an
   * alias, but you can override this
   *
   * @param alias - an alias to use when referring to fields of this table
   */
  Query<TId, TRow> query(String alias);

  /**
   * @return count of all rows in this table
   */
  int count();

  /**
   * @return count of all rows matching a given query
   */
  int count(Query<TId, TRow> query);

  /**
   * This is a short convenient method for obtaining field name from getter
   *
   * @param getter to obtain field name from
   * @return field name
   */
  String name(Function<TRow, ?> getter);

  /**
   * A short convenience method for building {@link OrderBy} instance without having to deal with
   * string literals for field names. Use its result to pass to query search methods.
   *
   * <p>NOTE: This is intended for simple queries only (as opposed to {@link JoinQuery}). When
   * building the latter, use respective methods from {@link Query} which participate in {@link
   * JoinQuery}. I.e.: {@link Query#orderBy(Function)}
   *
   * @param getter to get field name from
   * @return instance of {@link OrderByBuilder}, just call one of its methods {@link
   *     OrderByBuilder#asc()} or {@link OrderByBuilder#desc()} to needed instance of {@link
   *     OrderBy}
   */
  OrderByBuilder<TRow> orderBy(Function<TRow, ?> getter);

  /**
   * Parse {@link OrderBy} array from semi-colon separated values, i.e.
   * "karma,asc;title,asc;comment,asc".
   *
   * <p>NOTE: This is intended for simple queries only (as opposed to {@link JoinQuery}). When
   * building the latter, use respective methods {@link JoinQuery}, i.e. {@link
   * JoinQuery#parseOrderBy(String[])} or {@link JoinQuery#parseOrderBy(String)}
   */
  OrderBy[] parseOrderBy(String semicolonSeparatedValues);

  /**
   * Parse {@link OrderBy} array from individual order by statements ["karma,asc", "title,asc",
   * "comment,asc"]
   */
  OrderBy[] parseOrderBy(String[] orderByStr);

  /**
   * Create row
   *
   * @param row row to create
   * @return newly created row. It is possible that fields will differ due to ID set and other
   *     modifications set by {@link EasyCrudWireTap} injected into this service
   * @throws ValidationException in case of field validation errors. Some data access exceptions
   *     might be translated into field validation errors as well
   * @throws NotAuthorizedException if user is not authorized to perform this operation
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
   *     might be translated into field validation errors as well
   * @throws NotAuthorizedException if user is not authorized to perform this operation
   * @throws EntityNotFoundException in case entity does not exist
   */
  @Transactional(rollbackFor = Throwable.class)
  TRow update(TRow row);

  /**
   * @param id id of row to find
   * @return row or null if not found
   * @throws NotAuthorizedException if user is not authorized to perform this operation
   */
  TRow findById(TId id);

  /**
   * @param id id of row to get
   * @return row, never null. If row not found, throws {@link EntityNotFoundException}
   * @throws NotAuthorizedException if user is not authorized to perform this operation
   * @throws EntityNotFoundException in case entity does not exist
   */
  TRow getById(TId id);

  /**
   * @param orderBy optional orderBy, might be missing/null
   * @return results, might be empty, but never null
   * @throws NotAuthorizedException if user is not authorized to perform this operation
   */
  List<TRow> findAll(OrderBy... orderBy);

  /**
   * Deletes row by id.
   *
   * @param id id of the row to delete
   * @throws NotAuthorizedException if user is not authorized to perform this operation
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
   * @throws NotAuthorizedException if user is not authorized to perform this operation
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
   * @throws NotAuthorizedException if user is not authorized to perform this operation
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
   * @throws NotAuthorizedException if user is not authorized to perform this operation
   */
  @Transactional(rollbackFor = Throwable.class)
  int deleteByQuery(Query<TId, TRow> query);

  /**
   * @return class of Row served by this service
   */
  Class<TRow> getRowClass();

  /**
   * @return exception strategy that is used for handling exceptions for service methods
   */
  EasyCrudExceptionStrategy<TId, TRow> getExceptionStrategy();

  /**
   * @return entityTypeMessageCode. Same is used in exception messages codes {@link HasMessageCode}
   *     if thrown
   */
  String getRowMessageCode();

  /**
   * @return WireTap if any that is used by this service
   */
  EasyCrudWireTap<TRow> getWireTap();

  /**
   * Get several rows by their ids
   *
   * @param ids ids of rows to retrieve
   * @return list of rows or empty list if ids are empty or nothing found
   */
  default List<TRow> getByIds(Collection<TId> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }
    return query().in(TRow::getId, ids).findAll();
  }

  /**
   * Get several rows by their ids
   *
   * @param ids ids of rows to retrieve
   * @return a map of rows or empty map if ids are empty or nothing found. Map key is row id, and
   *     value is row itself
   */
  default Map<TId, TRow> getMapByIds(Collection<TId> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of();
    }
    return EasyCrudDtoUtils.toMapById(getByIds(ids));
  }

  /**
   * Get several rows by their ids. Ids are provided indirectly by providing source collection of
   * some objects and function to extract row id from items of given collection
   *
   * @param source collection of objects from which to retrieve IDs of rows to be loaded
   * @param idGetter a function which extracts row ID of source object
   * @param <TSource> type of source object
   * @return list of rows or empty list if ids are empty or nothing found
   */
  default <TSource> List<TRow> getByIds(
      Collection<TSource> source, Function<TSource, TId> idGetter) {
    if (CollectionUtils.isEmpty(source)) {
      return List.of();
    }
    List<TId> ids = source.stream().map(idGetter).filter(Objects::nonNull).distinct().toList();
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }
    return query().in(TRow::getId, ids).findAll();
  }

  /**
   * Get several rows by their ids. Ids are provided indirectly by providing source collection of
   * some objects and function to extract row id from items of given collection
   *
   * @param source collection of objects from which to retrieve IDs of rows to be loaded
   * @param idGetter a function which extracts row ID of source object
   * @param <TSource> type of source object
   * @return a map of rows or empty map if ids are empty or nothing found. Map key is row id, and
   *     value is row itself
   */
  default <TSource> Map<TId, TRow> getMapByIds(
      Collection<TSource> source, Function<TSource, TId> idGetter) {
    List<TRow> list = getByIds(source, idGetter);
    if (list.isEmpty()) {
      return Map.of();
    }
    return EasyCrudDtoUtils.toMapById(list);
  }

  /**
   * Builds new {@link JoinQuery} that can be used to make a query based on multiple tables
   * (including filtering, ordering and selection of data from them)
   *
   * @param query primary query to start with
   * @return join query
   */
  JoinQuery<TId, TRow> buildJoinQuery(Query<TId, TRow> query);

  PropertyNameResolver<TRow> getNameResolver();
}
