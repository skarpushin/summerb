package org.summerb.easycrud.join_query.impl;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import org.summerb.easycrud.join_query.model.JoinedRow;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;

@SuppressWarnings("unchecked")
public class JoinedRowImpl implements JoinedRow {
  protected Map<Query<?, ?>, Object> parts = new HashMap<>();

  @Override
  public <TId, TRow extends HasId<TId>> TRow get(Query<TId, TRow> query) {
    Preconditions.checkArgument(query != null, "query is required");
    return (TRow) parts.get(query);
  }

  public void put(Query<?, ?> query, Object row) {
    parts.put(query, row);
  }
}
