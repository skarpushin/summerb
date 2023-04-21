package org.summerb.easycrud.api.query.restrictions;

public class NumberLessOrEqualsRestriction extends NegatableRestrictionBase<Long> {
  private static final long serialVersionUID = -4260677481548959434L;

  private Long value;

  public NumberLessOrEqualsRestriction() {}

  public NumberLessOrEqualsRestriction(long value) {
    this.value = value;
  }

  public Long getValue() {
    return value;
  }

  public void setValue(Long value) {
    this.value = value;
  }

  @Override
  public boolean isMeet(Long subjectValue) {
    if (value == null && subjectValue == null) {
      return true;
    }
    if (value != null && subjectValue == null) {
      return isNegative();
    }
    if (value == null && subjectValue != null) {
      return !isNegative();
    }

    // NOTE: This is not 100% right since database might use case-sensitive
    // collation
    int result = value < subjectValue ? -1 : (value > subjectValue ? 1 : 0);
    return !isNegative() ? result <= 0 : result > 0;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    NumberLessOrEqualsRestriction other = (NumberLessOrEqualsRestriction) obj;
    if (value == null) {
      if (other.value != null) return false;
    } else if (!value.equals(other.value)) return false;
    return true;
  }
}
