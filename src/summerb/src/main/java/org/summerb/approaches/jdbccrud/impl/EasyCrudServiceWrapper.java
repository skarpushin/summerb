package org.summerb.approaches.jdbccrud.impl;

import org.summerb.approaches.jdbccrud.api.EasyCrudService;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.jdbccrud.api.exceptions.EntityNotFoundException;
import org.summerb.approaches.jdbccrud.api.query.OrderBy;
import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;

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
