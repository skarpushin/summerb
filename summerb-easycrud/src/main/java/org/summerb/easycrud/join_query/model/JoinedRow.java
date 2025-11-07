package org.summerb.easycrud.join_query.model;

import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;

public interface JoinedRow {
  /**
   * Retrieves row from the joined row by specifying the query that was used to assemble the {@link
   * org.summerb.easycrud.join_query.JoinQuery} that was used to retrieve the data
   *
   * @param query query that was used to configure {@link org.summerb.easycrud.join_query.JoinQuery}
   * @return row if present, or null if not present (i.e. if left join was used and DB returned
   *     nulls)
   * @param <TRow> row type
   * @throws IllegalArgumentException if query is null
   */
  <TId, TRow extends HasId<TId>> TRow get(Query<TId, TRow> query);

  /**
   * Retrieves row from the joined row by specifying Row class
   *
   * @param rowClass class of the row to retrieve
   * @return row if present, or null if not present (i.e. if left join was used and DB returned
   *     nulls)
   * @param <TRow> row type
   * @throws IllegalArgumentException if rowClass is null
   */
  <TId, TRow extends HasId<TId>> TRow get(Class<TRow> rowClass);
}
