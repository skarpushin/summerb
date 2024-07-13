package org.summerb.easycrud.api.query.restrictions;

import com.google.common.base.Preconditions;
import java.util.Objects;
import org.summerb.easycrud.api.query.restrictions.base.NegateableRestriction;

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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    StringLengthBetween that = (StringLengthBetween) o;
    return lowerBoundary == that.lowerBoundary && upperBoundary == that.upperBoundary;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), lowerBoundary, upperBoundary);
  }
}
