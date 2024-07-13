package org.summerb.easycrud.api.query;

import com.google.common.base.Preconditions;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    QueryConditions that = (QueryConditions) o;
    return Objects.equals(conditions, that.conditions);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(conditions);
  }
}
