package org.summerb.easycrud.api.query.restrictions;

import com.google.common.base.Preconditions;
import java.util.Objects;
import org.summerb.easycrud.api.query.restrictions.base.NegateableRestriction;

public class Between extends NegateableRestriction<Between> {
  protected Object lowerBoundary;
  protected Object upperBoundary;

  @SuppressWarnings({"rawtypes", "unchecked"})
  public Between(Object lowerBoundary, Object upperBoundary) {
    Preconditions.checkArgument(lowerBoundary != null, "lowerBoundary required");
    Preconditions.checkArgument(upperBoundary != null, "upperBoundary required");
    Preconditions.checkArgument(
        lowerBoundary.getClass().equals(upperBoundary.getClass()),
        "lowerBoundary and upperBoundary must be of a same class");

    if (lowerBoundary instanceof Comparable) {
      Preconditions.checkArgument(
          ((Comparable) lowerBoundary).compareTo(upperBoundary) < 0,
          "lowerBoundary must be less than upperBoundary");
    }

    this.lowerBoundary = lowerBoundary;
    this.upperBoundary = upperBoundary;
  }

  public Object getLowerBoundary() {
    return lowerBoundary;
  }

  public void setLowerBoundary(Object lowerBoundary) {
    this.lowerBoundary = lowerBoundary;
  }

  public Object getUpperBoundary() {
    return upperBoundary;
  }

  public void setUpperBoundary(Object upperBoundary) {
    this.upperBoundary = upperBoundary;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Between between = (Between) o;
    return Objects.equals(lowerBoundary, between.lowerBoundary)
        && Objects.equals(upperBoundary, between.upperBoundary);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), lowerBoundary, upperBoundary);
  }
}
