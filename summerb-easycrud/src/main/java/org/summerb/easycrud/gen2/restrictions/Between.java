package org.summerb.easycrud.gen2.restrictions;

import com.google.common.base.Preconditions;

public class Between extends NegateableRestrictionEx<Between> {

  public Between(Object lowerBoundary, Object upperBoundary) {
    Preconditions.checkArgument(lowerBoundary != null, "lowerBoundary required");
    Preconditions.checkArgument(upperBoundary != null, "upperBoundary required");
    // TODO Auto-generated constructor stub
  }
}
