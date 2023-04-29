/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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
import org.summerb.easycrud.api.dto.HasTimestamps;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.easycrud.api.exceptions.EntityNotFoundException;
import org.summerb.easycrud.api.exceptions.GenericEntityNotFoundException;
import org.summerb.easycrud.api.query.OrderBy;
import org.summerb.easycrud.api.query.Query;
import org.summerb.i18n.HasMessageCode;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.FieldValidationException;

/**
 * This service is intended for use with relatively simple Rows (DTO's). It
 * implements simple create-read-update-delete operations. Subclasses are
 * welcome to extend this functionality if needed as application evolves.
 * 
 * This service is perfect for simple Rows's like dictionaries. But it also can
 * be used for business logic.
 * 
 * EasyCrudService is Row-centric, it's good at working with 1 Row type only,
 * which is mapped to the table in the database. Each Row requires it's own
 * Service.
 * 
 * It's not an ORM framework, so if you need to have a reference to a user,
 * you'll create field like "long userId", but not "User user".
 * 
 * @author sergey.karpushin
 *
 * @param <TId>  type of primary key
 * @param <TRow> type of row
 */
public interface EasyCrudService<TId, TRow> {

	/**
	 * Create row
	 * 
	 * @return newly created row. It is possible that fields will differ due to ID
	 *         set and other modifications set by {@link EasyCrudWireTap} injected
	 *         into this service
	 * @throws FieldValidationException
	 * @throws NotAuthorizedException
	 */
	@Transactional(rollbackFor = Throwable.class)
	TRow create(TRow row);

	/**
	 * Update row
	 * 
	 * @return updated row. It is possible that fields will differ due to ID set and
	 *         other modifications set by {@link EasyCrudWireTap} injected into this
	 *         service
	 * @throws FieldValidationException
	 * @throws NotAuthorizedException
	 * @throws EntityNotFoundException
	 */
	@Transactional(rollbackFor = Throwable.class)
	TRow update(TRow row);

	/**
	 * @return row or null if not found
	 * 
	 * @throws NotAuthorizedException
	 */
	TRow findById(TId id);

	/**
	 * @return row, never null. If row not found, throws
	 *         {@link GenericEntityNotFoundException}
	 * 
	 * @throws NotAuthorizedException
	 * @throws GenericEntityNotFoundException
	 */
	TRow getById(TId id);

	/**
	 * @return Row or null if not found. If more than 1 row matched query exception
	 *         will be thrown
	 * 
	 * @throws NotAuthorizedException
	 */
	TRow findOneByQuery(Query query);

	/**
	 * @return row, never null. If nothing found, throws
	 *         {@link GenericEntityNotFoundException}
	 * 
	 * @throws NotAuthorizedException
	 * @throws GenericEntityNotFoundException
	 */
	TRow getFirstByQuery(Query query, OrderBy... orderBy);

	/**
	 * @return results, might be empty, but never null
	 * 
	 * @throws NotAuthorizedException
	 */
	PaginatedList<TRow> find(PagerParams pagerParams, Query optionalQuery, OrderBy... orderBy);

	/**
	 * @return results, might be empty, but never null
	 * 
	 * @param optionalQuery - optional {@link Query}. If null, then this call is
	 *                      same as {@link #findAll(OrderBy...)}
	 * 
	 * @throws NotAuthorizedException
	 */
	List<TRow> findAll(Query optionalQuery, OrderBy... orderBy);

	/**
	 * @return results, might be empty, but never null
	 * 
	 * @throws NotAuthorizedException
	 */
	List<TRow> findAll(OrderBy... orderBy);

	/**
	 * Deletes row by id.
	 * 
	 * @throws NotAuthorizedException
	 * @throws EntityNotFoundException
	 * @throws GenericEntityNotFoundException
	 */
	@Transactional(rollbackFor = Throwable.class)
	void deleteById(TId id);

	/**
	 * Deletes row by example. If TRow implements {@link HasTimestamps} then this
	 * call is equivalent to {@link #deleteByIdOptimistic(Object, long)}
	 * 
	 * @throws NotAuthorizedException
	 * @throws EntityNotFoundException
	 * @throws GenericEntityNotFoundException
	 */
	@Transactional(rollbackFor = Throwable.class)
	void delete(TRow row);

	/**
	 * Delete row by id and modifiedAt. Applicable only in cases when row class
	 * extends {@link HasTimestamps}.
	 * 
	 * @throws NotAuthorizedException
	 * @throws EntityNotFoundException
	 * @throws GenericEntityNotFoundException
	 * @throws OptimisticLockingFailureException if row version doesn't match
	 *                                           provided one
	 */
	@Transactional(rollbackFor = Throwable.class)
	void deleteByIdOptimistic(TId id, long modifiedAt);

	/**
	 * Delete rows by query
	 * 
	 * @param query
	 * @return number of affected rows
	 * @throws NotAuthorizedException
	 */
	@Transactional(rollbackFor = Throwable.class)
	int deleteByQuery(Query query);

	/**
	 * @return class of Row served by this service
	 */
	Class<TRow> getRowClass();

	/**
	 * @return entityTypeMessageCode. Same is used in exception messages codes
	 *         {@link HasMessageCode} if thrown
	 */
	String getRowMessageCode();

}
