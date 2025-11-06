package org.summerb.easycrud.join_query;

import java.util.List;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;

public interface SelectFactory {
  /** Builds selector that can invoke joined select but will select/deserialize only 1 row type */
  <TRow extends HasId<TId>, TId> Select<TId, TRow> build(
      JoinQuery<?, ?> joinQuery, Query<TId, TRow> entityToSelect);

  /** Builds selector that can invoke joined select and select/deserialize several row types */
  JoinedSelect build(JoinQuery<?, ?> joinQuery, List<Query<?, ?>> entitiesToSelect);
}
