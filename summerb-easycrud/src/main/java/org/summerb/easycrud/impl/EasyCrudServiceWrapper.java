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
package org.summerb.easycrud.impl;

import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.easycrud.api.exceptions.EntityNotFoundException;
import org.summerb.easycrud.api.query.OrderBy;
import org.summerb.easycrud.api.query.Query;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.FieldValidationException;

import com.google.common.base.Preconditions;

public class EasyCrudServiceWrapper<TId, TDto, TActual extends EasyCrudService<TId, TDto>>
		implements EasyCrudService<TId, TDto> {
	protected TActual actual;

	public EasyCrudServiceWrapper(TActual actual) {
		Preconditions.checkArgument(actual != null);
		this.actual = actual;
	}

	@Override
	public TDto create(TDto dto) throws FieldValidationException, NotAuthorizedException {
		return actual.create(dto);
	}

	@Override
	public TDto update(TDto dto) throws FieldValidationException, NotAuthorizedException, EntityNotFoundException {
		return actual.update(dto);
	}

	@Override
	public TDto findById(TId id) throws NotAuthorizedException {
		return actual.findById(id);
	}

	@Override
	public TDto findOneByQuery(Query query) throws NotAuthorizedException {
		return actual.findOneByQuery(query);
	}

	@Override
	public PaginatedList<TDto> query(PagerParams pagerParams, Query optionalQuery, OrderBy... orderBy)
			throws NotAuthorizedException {
		return actual.query(pagerParams, optionalQuery, orderBy);
	}

	@Override
	public void deleteById(TId id) throws NotAuthorizedException, EntityNotFoundException {
		actual.deleteById(id);
	}

	@Override
	public void deleteByIdOptimistic(TId id, long modifiedAt) throws NotAuthorizedException, EntityNotFoundException {
		actual.deleteByIdOptimistic(id, modifiedAt);
	}

	@Override
	public int deleteByQuery(Query query) throws NotAuthorizedException {
		return actual.deleteByQuery(query);
	}

	@Override
	public Class<TDto> getDtoClass() {
		return actual.getDtoClass();
	}

	@Override
	public String getEntityTypeMessageCode() {
		return actual.getEntityTypeMessageCode();
	}

}
