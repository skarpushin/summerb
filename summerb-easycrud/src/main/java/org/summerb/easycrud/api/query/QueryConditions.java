package org.summerb.easycrud.api.query;

import com.google.common.base.Preconditions;
import java.util.LinkedList;
import java.util.List;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.query.restrictions.base.Restriction;

public class QueryConditions {
  protected final List<Condition> conditions = new LinkedList<>();

  public QueryConditions() {
    super();
  }

  public List<Condition> getConditions() {
    return conditions;
  }

  public void add(Condition condition) {
    Preconditions.checkArgument(condition != null, "condition required");
    conditions.add(condition);
  }

  public void add(String fieldName, Restriction restriction) {
    Preconditions.checkArgument(StringUtils.hasText(fieldName), "fieldName required");
    Preconditions.checkArgument(restriction != null, "restriction required");
    add(new FieldCondition(fieldName, restriction));
  }

  public boolean isEmpty() {
    return conditions.isEmpty();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + conditions.hashCode();
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
    QueryConditions other = (QueryConditions) obj;
    if (!conditions.equals(other.conditions)) {
      return false;
    }
    return true;
  }
}
