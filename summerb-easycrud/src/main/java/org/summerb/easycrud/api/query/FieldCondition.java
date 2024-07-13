package org.summerb.easycrud.api.query;

import java.util.Objects;
import org.summerb.easycrud.api.query.restrictions.base.Restriction;

public class FieldCondition extends Condition {

  protected String fieldName;
  protected Restriction restriction;

  public FieldCondition(String fieldName, Restriction restriction) {
    this.fieldName = fieldName;
    this.restriction = restriction;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public Restriction getRestriction() {
    return restriction;
  }

  public void setRestriction(Restriction restriction) {
    this.restriction = restriction;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FieldCondition that = (FieldCondition) o;
    return Objects.equals(fieldName, that.fieldName)
        && Objects.equals(restriction, that.restriction);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldName, restriction);
  }
}
