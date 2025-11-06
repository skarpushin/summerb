package org.summerb.easycrud.join_query;

import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;

public interface JoinQueryFactory {
  <TId, TRow extends HasId<TId>> JoinQuery<TId, TRow> build(Query<TId, TRow> primaryQuery);
}
