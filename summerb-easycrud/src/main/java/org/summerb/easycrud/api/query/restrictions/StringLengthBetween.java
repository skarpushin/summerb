package org.summerb.easycrud.api.query.restrictions;

import org.summerb.easycrud.api.query.restrictions.base.NegateableRestriction;

import com.google.common.base.Preconditions;

public class StringLengthBetween extends NegateableRestriction<StringLengthBetween> {

  protected int lowerBoundary;
  protected int upperBoundary;

  public StringLengthBetween(int lowerBoundary, int upperBoundary) {
    Preconditions.checkArgument(
        lowerBoundary < upperBoundary, "Lower boundary must be less than upperBoundary");
    this.lowerBoundary = lowerBoundary;
    this.upperBoundary = upperBoundary;
  }

  public int getLowerBoundary() {
    return lowerBoundary;
  }

  public void setLowerBoundary(int lowerBoundary) {
    this.lowerBoundary = lowerBoundary;
  }

  public int getUpperBoundary() {
    return upperBoundary;
  }

  public void setUpperBoundary(int upperBoundary) {
    this.upperBoundary = upperBoundary;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + lowerBoundary;
    result = prime * result + upperBoundary;
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
    StringLengthBetween other = (StringLengthBetween) obj;
    if (lowerBoundary != other.lowerBoundary) {
      return false;
    }
    if (upperBoundary != other.upperBoundary) {
      return false;
    }
    return true;
  }
}
