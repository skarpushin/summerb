package org.summerb.easycrud.query;

import org.summerb.easycrud.gen2.restrictions.RestrictionEx;

public class FieldConditionEx extends ConditionEx {

  protected String fieldName;
  protected RestrictionEx restriction;

  public FieldConditionEx(String fieldName, RestrictionEx restriction) {
    this.fieldName = fieldName;
    this.restriction = restriction;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public RestrictionEx getRestriction() {
    return restriction;
  }

  public void setRestriction(RestrictionEx restriction) {
    this.restriction = restriction;
  }
}
