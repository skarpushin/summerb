package org.summerb.approaches.jdbccrud.api;

import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.jdbccrud.api.query.OrderBy;
import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.validation.FieldValidationException;

/**
 * 
 * @author sergey.karpushin
 *
 */
public interface EasyCrudDao<TId, TDto extends HasId<TId>> {
	void create(TDto dto) throws FieldValidationException;

	TDto findById(TId id);

	TDto findOneByQuery(Query query);

	int delete(TId id);

	int delete(TId id, long modifiedAt);

	int update(TDto dto) throws FieldValidationException;

	int deleteByQuery(Query query);

	PaginatedList<TDto> query(PagerParams pagerParams, Query optionalQuery, OrderBy... orderBy);
}
