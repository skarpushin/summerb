package org.summerb.easycrud.query.restrictions.base;

public abstract class NegateableRestriction<T> extends Restriction {

  protected boolean not;

  @SuppressWarnings("unchecked")
  public T not() {
    not = true;
    return (T) this;
  }

  public boolean isNot() {
    return not;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (not ? 1231 : 1237);
    return result;
  }

  @SuppressWarnings("rawtypes")
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
    NegateableRestriction other = (NegateableRestriction) obj;
    if (not != other.not) {
      return false;
    }
    return true;
  }
}
