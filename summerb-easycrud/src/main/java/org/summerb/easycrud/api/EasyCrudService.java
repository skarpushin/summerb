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

import org.springframework.transaction.annotation.Transactional;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.easycrud.api.exceptions.EntityNotFoundException;
import org.summerb.easycrud.api.query.OrderBy;
import org.summerb.easycrud.api.query.Query;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.FieldValidationException;

/**
 * This service is intended for use with relatively simple DTO's. It implements
 * simple create-read-update-delete operations. Subclasses are welcome to extend
 * this functionality if needed as application evolves.
 * 
 * This service is perfect for simple DTO's like dictionaries. But it also can
 * be used for business logic to some extent.
 * 
 * EasyCrudService is DTO-centric, it's good at working with 1 DTO only, which
 * is mapped to the table in the database. Each DTO requires it's own Service.
 * 
 * It's not an ORM framework, so if you need to have a reference to a user,
 * you'll create field like "long userId", but not "User user".
 * 
 * @author sergey.karpushin
 *
 * @param <TId>
 *            type of primary key
 * @param <TDto>
 *            type of dto
 */
public interface EasyCrudService<TId, TDto> {

	@Transactional(rollbackFor = Throwable.class)
	TDto create(TDto dto) throws FieldValidationException, NotAuthorizedException;

	@Transactional(rollbackFor = Throwable.class)
	TDto update(TDto dto) throws FieldValidationException, NotAuthorizedException, EntityNotFoundException;

	TDto findById(TId id) throws NotAuthorizedException;

	TDto findOneByQuery(Query query) throws NotAuthorizedException;

	PaginatedList<TDto> query(PagerParams pagerParams, Query optionalQuery, OrderBy... orderBy)
			throws NotAuthorizedException;

	@Transactional(rollbackFor = Throwable.class)
	void deleteById(TId id) throws NotAuthorizedException, EntityNotFoundException;

	@Transactional(rollbackFor = Throwable.class)
	void deleteByIdOptimistic(TId id, long modifiedAt) throws NotAuthorizedException, EntityNotFoundException;

	@Transactional(rollbackFor = Throwable.class)
	int deleteByQuery(Query query) throws NotAuthorizedException;

	Class<TDto> getDtoClass();

	String getEntityTypeMessageCode();

}
