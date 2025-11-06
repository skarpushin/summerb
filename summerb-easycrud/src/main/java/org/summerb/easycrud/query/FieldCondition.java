package org.summerb.easycrud.query;

import org.summerb.easycrud.query.restrictions.base.Restriction;

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
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
    result = prime * result + ((restriction == null) ? 0 : restriction.hashCode());
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
    FieldCondition other = (FieldCondition) obj;
    if (fieldName == null) {
      if (other.fieldName != null) {
        return false;
      }
    } else if (!fieldName.equals(other.fieldName)) {
      return false;
    }
    if (restriction == null) {
      if (other.restriction != null) {
        return false;
      }
    } else if (!restriction.equals(other.restriction)) {
      return false;
    }
    return true;
  }
}
