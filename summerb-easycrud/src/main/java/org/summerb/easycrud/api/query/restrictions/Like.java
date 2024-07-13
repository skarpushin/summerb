package org.summerb.easycrud.api.query.restrictions;

import com.google.common.base.Preconditions;
import java.util.Objects;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.query.restrictions.base.NegateableRestriction;

public class Like extends NegateableRestriction<Like> {

  protected String subString;
  protected boolean addPrefixWildcard;
  protected boolean addPostfixWildcard;

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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Like like = (Like) o;
    return addPrefixWildcard == like.addPrefixWildcard
        && addPostfixWildcard == like.addPostfixWildcard
        && Objects.equals(subString, like.subString);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), subString, addPrefixWildcard, addPostfixWildcard);
  }
}
