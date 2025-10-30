package org.summerb.easycrud.api.query;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Objects;
import org.springframework.util.CollectionUtils;
import org.summerb.easycrud.api.row.HasId;

public class DisjunctionCondition<TId, TRow extends HasId<TId>> extends Condition {

  protected List<Query<TId, TRow>> queries;

  public DisjunctionCondition(List<Query<TId, TRow>> disjunctions) {
    Preconditions.checkArgument(
        !CollectionUtils.isEmpty(disjunctions), "non-empty queries required");
    this.queries = disjunctions;
  }

  public List<Query<TId, TRow>> getQueries() {
    return queries;
  }

  public void setQueries(List<Query<TId, TRow>> queries) {
    this.queries = queries;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    DisjunctionCondition<?, ?> that = (DisjunctionCondition<?, ?>) o;
    return Objects.equals(queries, that.queries);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(queries);
  }
}
