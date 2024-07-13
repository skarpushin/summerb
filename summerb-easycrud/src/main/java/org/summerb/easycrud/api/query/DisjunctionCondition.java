package org.summerb.easycrud.api.query;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Objects;
import org.springframework.util.CollectionUtils;

public class DisjunctionCondition extends Condition {

  protected List<? extends QueryConditions> queries;

  public DisjunctionCondition(List<? extends QueryConditions> disjunctions) {
    Preconditions.checkArgument(
        !CollectionUtils.isEmpty(disjunctions), "non-empty queries required");
    this.queries = disjunctions;
  }

  public List<? extends QueryConditions> getQueries() {
    return queries;
  }

  public void setQueries(List<? extends QueryConditions> queries) {
    this.queries = queries;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DisjunctionCondition that = (DisjunctionCondition) o;
    return Objects.equals(queries, that.queries);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(queries);
  }
}
