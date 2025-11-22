package org.summerb.easycrud.sql_builder;

import java.util.List;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.sql_builder.model.ColumnsSelection;

public interface OrderByToSql {
  /**
   * Build ORDER BY sub-clause for given array of orderBy requests
   *
   * @return comma-separated list of order by statements prefixed with "ORDER BY", or empty string
   *     if no order bys provided
   */
  String buildOrderBySubclause(OrderBy[] orderBy);

  /**
   * Append to given StringBuilder comma-separated list of order by elements for the given join
   * query
   */
  void appendOrderByElements(
      OrderBy[] orderByArr,
      JoinQuery<?, ?> joinQuery,
      List<ColumnsSelection> columnSelections,
      StringBuilder ret);
}
