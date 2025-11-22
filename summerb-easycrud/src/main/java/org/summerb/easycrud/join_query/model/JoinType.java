package org.summerb.easycrud.join_query.model;

public enum JoinType {
  INNER,
  LEFT,

  /** Obviously this is not a join. But since this is often called "anti-join" it is present here */
  NOT_EXISTS,

  /**
   * Obviously this is not a join. But since we might want to filter by joined tables but at the
   * same time avoid ending up with cartesian products due to one-to-many relationship, it is
   * present here
   */
  EXISTS

  // NOTE: Not adding "right join" intentionally -- almost never seen this in production code
}
