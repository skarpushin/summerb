package org.summerb.easycrud.api.query.restrictions;

import org.summerb.easycrud.api.query.restrictions.base.NegateableRestriction;

import com.google.common.base.Preconditions;

public class Less extends NegateableRestriction<Less> {
  protected Object value;
  protected boolean includeBoundary;

  public Less(Object value, boolean includeBoundary) {
    Preconditions.checkArgument(value != null, "Non null value expected");
    this.value = value;
    this.includeBoundary = includeBoundary;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
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
    result = prime * result + ((value == null) ? 0 : value.hashCode());
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
    Less other = (Less) obj;
    if (includeBoundary != other.includeBoundary) {
      return false;
    }
    if (value == null) {
      if (other.value != null) {
        return false;
      }
    } else if (!value.equals(other.value)) {
      return false;
    }
    return true;
  }
}
