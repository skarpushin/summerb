package org.summerb.easycrud.api;

import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.easycrud.api.query.OrderBy;
import org.summerb.easycrud.api.query.Query;

/**
 * 
 * @author sergey.karpushin
 *
 */public interface EasyCrudDao<TId, TDto extends HasId<TId>> {
	void create(TDto dto);

	TDto findById(TId id);

	TDto findOneByQuery(Query query);

	int delete(TId id);

	int delete(TId id, long modifiedAt);

	int update(TDto dto);

	int deleteByQuery(Query query);

	PaginatedList<TDto> query(PagerParams pagerParams, Query optionalQuery, OrderBy... orderBy);
}
