package org.summerb.easycrud.join_query;

import java.util.List;
import org.summerb.easycrud.join_query.model.JoinedRow;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;

/** Represents selection of several entities in a single query */
public interface JoinedSelect {
  JoinedRow findOne();

  JoinedRow getOne();

  JoinedRow getFirst(OrderBy... orderBy);

  JoinedRow findFirst(OrderBy... orderBy);

  PaginatedList<JoinedRow> find(PagerParams pagerParams, OrderBy... orderBy);

  List<JoinedRow> findAll(OrderBy... orderBy);

  List<JoinedRow> getAll(OrderBy... orderBy);

  int count();
}
