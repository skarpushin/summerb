package org.summerb.easycrud.query.restrictions;

import com.google.common.base.Preconditions;
import org.summerb.easycrud.query.restrictions.base.NegateableRestriction;

/** Restriction for BETWEEN clause. */
public class Between extends NegateableRestriction<Between> {
  /** Lower boundary */
  protected Object lowerBoundary;

  /** Upper boundary */
  protected Object upperBoundary;

  /**
   * Constructor for Between restriction.
   *
   * @param lowerBoundary lower boundary
   * @param upperBoundary upper boundary
   */
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

  /**
   * @return lower boundary
   */
  public Object getLowerBoundary() {
    return lowerBoundary;
  }

  /**
   * @param lowerBoundary lower boundary
   */
  public void setLowerBoundary(Object lowerBoundary) {
    this.lowerBoundary = lowerBoundary;
  }

  /**
   * @return upper boundary
   */
  public Object getUpperBoundary() {
    return upperBoundary;
  }

  /**
   * @param upperBoundary upper boundary
   */
  public void setUpperBoundary(Object upperBoundary) {
    this.upperBoundary = upperBoundary;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((lowerBoundary == null) ? 0 : lowerBoundary.hashCode());
    result = prime * result + ((upperBoundary == null) ? 0 : upperBoundary.hashCode());
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
    Between other = (Between) obj;
    if (lowerBoundary == null) {
      if (other.lowerBoundary != null) {
        return false;
      }
    } else if (!lowerBoundary.equals(other.lowerBoundary)) {
      return false;
    }
    if (upperBoundary == null) {
      if (other.upperBoundary != null) {
        return false;
      }
    } else if (!upperBoundary.equals(other.upperBoundary)) {
      return false;
    }
    return true;
  }
}
