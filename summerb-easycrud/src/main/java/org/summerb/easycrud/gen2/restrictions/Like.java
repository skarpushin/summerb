package org.summerb.easycrud.gen2.restrictions;

import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;

public class Like extends NegateableRestrictionEx<Like> {

  public Like(String subString, boolean addPrefixWildcard, boolean addPostfixWildcard) {
    Preconditions.checkArgument(StringUtils.hasText(subString), "subString required");

    // TODO Auto-generated constructor stub
  }

  public Like(String subString) {
    Preconditions.checkArgument(StringUtils.hasText(subString), "subString required");

    // TODO Auto-generated constructor stub
  }
}
