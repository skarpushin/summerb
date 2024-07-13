package org.summerb.easycrud.api.query.restrictions.base;

import java.util.Objects;

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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    NegateableRestriction<?> that = (NegateableRestriction<?>) o;
    return not == that.not;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), not);
  }
}
