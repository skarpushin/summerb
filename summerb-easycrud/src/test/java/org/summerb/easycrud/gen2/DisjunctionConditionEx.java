package org.summerb.easycrud.gen2;

import java.util.Collection;

import org.springframework.util.CollectionUtils;
import org.summerb.easycrud.query.ConditionEx;

import com.google.common.base.Preconditions;

public class DisjunctionConditionEx<T> extends ConditionEx {

  private Collection<QueryEx<T>> disjunctions;

  public DisjunctionConditionEx(Collection<QueryEx<T>> disjunctions) {
    Preconditions.checkArgument(
        !CollectionUtils.isEmpty(disjunctions), "non-empty disjunctions required");
    this.disjunctions = disjunctions;
  }

  public Collection<QueryEx<T>> getDisjunctions() {
    return disjunctions;
  }

  public void setDisjunctions(Collection<QueryEx<T>> disjunctions) {
    this.disjunctions = disjunctions;
  }
}
