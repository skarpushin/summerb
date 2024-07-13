package org.summerb.easycrud.api.query.restrictions;

import com.google.common.base.Preconditions;
import java.util.Objects;
import org.summerb.easycrud.api.query.restrictions.base.NegateableRestriction;

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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    StringLengthLess that = (StringLengthLess) o;
    return value == that.value && includeBoundary == that.includeBoundary;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value, includeBoundary);
  }
}
