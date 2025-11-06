package org.summerb.easycrud.join_query.impl;

import com.google.common.base.Preconditions;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.join_query.model.JoinType;
import org.summerb.easycrud.query.Query;

public class JoinQueryElement {
  protected final JoinType joinType;

  /** Query that is based on the Row (table) that is referring to some other table */
  protected final Query<?, ?> referer;

  /** Name of the field from the referer query which contains ID of the referred */
  protected final String otherIdGetterFieldName;

  /** Query that is based on the Row (table) that is being referred to */
  protected final Query<?, ?> referred;

  public JoinQueryElement(
      JoinType joinType, Query<?, ?> referer, String otherIdGetterFieldName, Query<?, ?> referred) {
    Preconditions.checkNotNull(joinType, "joinType is required");
    Preconditions.checkNotNull(referer, "referer is required");
    Preconditions.checkArgument(
        StringUtils.hasText(otherIdGetterFieldName), "otherIdGetterFieldName is required");
    Preconditions.checkNotNull(referred, "referred is required");

    this.joinType = joinType;
    this.referer = referer;
    this.otherIdGetterFieldName = otherIdGetterFieldName;
    this.referred = referred;
  }

  public JoinType getJoinType() {
    return joinType;
  }

  public String getOtherIdGetterFieldName() {
    return otherIdGetterFieldName;
  }

  public Query<?, ?> getReferer() {
    return referer;
  }

  public Query<?, ?> getReferred() {
    return referred;
  }
}
