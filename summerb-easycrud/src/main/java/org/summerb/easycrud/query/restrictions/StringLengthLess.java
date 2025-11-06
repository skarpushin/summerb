package org.summerb.easycrud.query.restrictions;

import com.google.common.base.Preconditions;
import org.summerb.easycrud.query.restrictions.base.NegateableRestriction;

public class StringLengthLess extends NegateableRestriction<StringLengthLess> {

  protected int value;
  protected boolean includeBoundary;

  public StringLengthLess(int value, boolean includeBoundary) {
    if (includeBoundary) {
      Preconditions.checkArgument(value >= 0, "value must be non-negative");
    } else {
      Preconditions.checkArgument(value > 0, "value must be positive");
    }

    this.value = value;
    this.includeBoundary = includeBoundary;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public boolean isIncludeBoundary() {
    return includeBoundary;
  }

  public void setIncludeBoundary(boolean includeBoundary) {
    this.includeBoundary = includeBoundary;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (includeBoundary ? 1231 : 1237);
    result = prime * result + value;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    StringLengthLess other = (StringLengthLess) obj;
    if (includeBoundary != other.includeBoundary) {
      return false;
    }
    if (value != other.value) {
      return false;
    }
    return true;
  }
}
