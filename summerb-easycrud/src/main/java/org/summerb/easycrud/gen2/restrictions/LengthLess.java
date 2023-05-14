package org.summerb.easycrud.gen2.restrictions;

import com.google.common.base.Preconditions;

public class LengthLess extends NegateableRestrictionEx<LengthLess> {

  public LengthLess(int value, boolean includeBoundary) {
    if (includeBoundary) {
      Preconditions.checkArgument(value >= 0, "value must be non-negative");
    } else {
      Preconditions.checkArgument(value > 0, "value must be positive");
    }

    // TODO Auto-generated constructor stub
  }
}
