package org.summerb.easycrud.api.query.restrictions;

import org.summerb.easycrud.api.query.restrictions.base.NegateableRestriction;

import com.google.common.base.Preconditions;

public class Equals extends NegateableRestriction<Equals> {

  private Object value;

  public Equals(Object value) {
    Preconditions.checkArgument(value != null, "value required");
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
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
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Equals other = (Equals) obj;
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
