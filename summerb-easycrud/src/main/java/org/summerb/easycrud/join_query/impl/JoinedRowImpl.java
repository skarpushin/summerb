package org.summerb.easycrud.join_query.impl;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import org.summerb.easycrud.join_query.model.JoinedRow;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;

@SuppressWarnings("unchecked")
public class JoinedRowImpl implements JoinedRow {
  public static final int INITIAL_CAPACITY = 3;

  protected Map<Query<?, ?>, Object> partsByQuery;
  protected Map<Class<?>, Object> partsByRowClass;

  public JoinedRowImpl() {
    partsByQuery = new HashMap<>(INITIAL_CAPACITY);
    partsByRowClass = new HashMap<>(INITIAL_CAPACITY);
  }

  public JoinedRowImpl(int size) {
    partsByQuery = new HashMap<>(size);
    partsByRowClass = new HashMap<>(size);
  }

  public void put(Query<?, ?> query, Object row) {
    partsByQuery.put(query, row);

    if (row != null) {
      partsByRowClass.put(row.getClass(), row);
    }
  }

  @Override
  public <TId, TRow extends HasId<TId>> TRow get(Query<TId, TRow> query) {
    Preconditions.checkArgument(query != null, "query is required");
    return (TRow) partsByQuery.get(query);
  }

  @Override
  public <TId, TRow extends HasId<TId>> TRow get(Class<TRow> rowClass) {
    Preconditions.checkArgument(rowClass != null, "rowClass is required");
    return (TRow) partsByRowClass.get(rowClass);
  }
}
