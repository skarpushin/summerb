package org.summerb.easycrud.gen2.restrictions;

public abstract class NegateableRestrictionEx<T> extends RestrictionEx {

  protected boolean not;

  @SuppressWarnings("unchecked")
  public T not() {
    not = true;
    return (T) this;
  }

  public boolean isNot() {
    return not;
  }
}
