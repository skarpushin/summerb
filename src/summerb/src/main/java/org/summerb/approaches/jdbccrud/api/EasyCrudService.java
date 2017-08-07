package org.summerb.approaches.jdbccrud.api;

import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.jdbccrud.api.exceptions.EntityNotFoundException;
import org.summerb.approaches.jdbccrud.api.query.OrderBy;
import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;

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
	TDto create(TDto dto) throws FieldValidationException, NotAuthorizedException;

	TDto update(TDto dto) throws FieldValidationException, NotAuthorizedException, EntityNotFoundException;

	TDto findById(TId id) throws NotAuthorizedException;

	TDto findOneByQuery(Query query) throws NotAuthorizedException;

	PaginatedList<TDto> query(PagerParams pagerParams, Query optionalQuery, OrderBy... orderBy)
			throws NotAuthorizedException;

	void deleteById(TId id) throws NotAuthorizedException, EntityNotFoundException;

	void deleteByIdOptimistic(TId id, long modifiedAt) throws NotAuthorizedException, EntityNotFoundException;

	int deleteByQuery(Query query) throws NotAuthorizedException;

	Class<TDto> getDtoClass();

	String getEntityTypeMessageCode();
}