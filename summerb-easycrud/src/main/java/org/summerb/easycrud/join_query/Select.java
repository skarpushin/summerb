package org.summerb.easycrud.join_query;

import java.util.List;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.row.HasId;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;

/**
 * Represents selection of single Row type
 *
 * @param <TRow>
 */
public interface Select<TId, TRow extends HasId<TId>> {
  TRow findOne();

  TRow getOne();

  TRow getFirst(OrderBy... orderBy);

  TRow findFirst(OrderBy... orderBy);

  PaginatedList<TRow> find(PagerParams pagerParams, OrderBy... orderBy);

  List<TRow> findPage(PagerParams pagerParams, OrderBy... orderBy);

  List<TRow> findAll(OrderBy... orderBy);

  List<TRow> getAll(OrderBy... orderBy);

  int count();
}
