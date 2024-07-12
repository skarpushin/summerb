package org.summerb.easycrud.api.query;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.google.common.base.Preconditions;

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
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((queries == null) ? 0 : queries.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    DisjunctionCondition other = (DisjunctionCondition) obj;
    if (queries == null) {
      if (other.queries != null) {
        return false;
      }
    } else if (!queries.equals(other.queries)) {
      return false;
    }
    return true;
  }
}
