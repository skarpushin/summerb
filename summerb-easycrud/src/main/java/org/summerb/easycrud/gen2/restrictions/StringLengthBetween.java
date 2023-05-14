package org.summerb.easycrud.gen2.restrictions;

import com.google.common.base.Preconditions;

public class StringLengthBetween extends NegateableRestrictionEx<StringLengthBetween> {

  public StringLengthBetween(Number lowerBoundary, Number upperBoundary) {
    Preconditions.checkArgument(lowerBoundary != null, "lowerBoundary required");
    Preconditions.checkArgument(upperBoundary != null, "upperBoundary required");
    // TODO Auto-generated constructor stub
  }
}
