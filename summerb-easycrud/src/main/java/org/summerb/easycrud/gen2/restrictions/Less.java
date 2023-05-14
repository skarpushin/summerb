package org.summerb.easycrud.gen2.restrictions;

import com.google.common.base.Preconditions;

public class Less extends NegateableRestrictionEx<Less> {

  public Less(Object value, boolean includesBoundary) {
    Preconditions.checkArgument(value != null, "Non null value expected");
    // TODO Auto-generated constructor stub
  }
}
