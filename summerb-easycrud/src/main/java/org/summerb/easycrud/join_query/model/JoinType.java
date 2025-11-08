package org.summerb.easycrud.join_query.model;

public enum JoinType {
  INNER,
  LEFT,

  /** Obviously this is not a join. But since this is often called "anti-join" it is present here */
  NOT_EXISTS

  // NOTE: Not adding "right join" intentionally -- almost never seen this in production code
}
