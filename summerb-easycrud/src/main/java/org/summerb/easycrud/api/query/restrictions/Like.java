package org.summerb.easycrud.api.query.restrictions;

import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.query.restrictions.base.NegateableRestriction;

import com.google.common.base.Preconditions;

public class Like extends NegateableRestriction<Like> {

  private String subString;
  private boolean addPrefixWildcard;
  private boolean addPostfixWildcard;

  public Like(String subString, boolean addPrefixWildcard, boolean addPostfixWildcard) {
    Preconditions.checkArgument(StringUtils.hasText(subString), "subString required");
    this.subString = subString;
    this.addPrefixWildcard = addPrefixWildcard;
    this.addPostfixWildcard = addPostfixWildcard;
  }

  public Like(String subString) {
    Preconditions.checkArgument(StringUtils.hasText(subString), "subString required");
    this.subString = subString;
  }

  public String getSubString() {
    return subString;
  }

  public void setSubString(String subString) {
    this.subString = subString;
  }

  public boolean isAddPrefixWildcard() {
    return addPrefixWildcard;
  }

  public void setAddPrefixWildcard(boolean addPrefixWildcard) {
    this.addPrefixWildcard = addPrefixWildcard;
  }

  public boolean isAddPostfixWildcard() {
    return addPostfixWildcard;
  }

  public void setAddPostfixWildcard(boolean addPostfixWildcard) {
    this.addPostfixWildcard = addPostfixWildcard;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (addPostfixWildcard ? 1231 : 1237);
    result = prime * result + (addPrefixWildcard ? 1231 : 1237);
    result = prime * result + ((subString == null) ? 0 : subString.hashCode());
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
    Like other = (Like) obj;
    if (addPostfixWildcard != other.addPostfixWildcard) {
      return false;
    }
    if (addPrefixWildcard != other.addPrefixWildcard) {
      return false;
    }
    if (subString == null) {
      if (other.subString != null) {
        return false;
      }
    } else if (!subString.equals(other.subString)) {
      return false;
    }
    return true;
  }
}
