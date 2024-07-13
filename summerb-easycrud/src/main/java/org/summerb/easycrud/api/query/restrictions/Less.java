package org.summerb.easycrud.api.query.restrictions;

import com.google.common.base.Preconditions;
import java.util.Objects;
import org.summerb.easycrud.api.query.restrictions.base.NegateableRestriction;

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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Less less = (Less) o;
    return includeBoundary == less.includeBoundary && Objects.equals(value, less.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value, includeBoundary);
  }
}
