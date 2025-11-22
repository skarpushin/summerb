package org.summerb.easycrud.join_query;

public enum JoinDirection {
  /**
   * When joined table is referenced by a previously selected table. This type of join would not
   * introduce cartesian products.
   */
  FORWARD,

  /**
   * When a previously selected table referenced by a joined table. This type of join might
   * introduce cartesian products in the case of one-to-many relationships.
   */
  BACKWARD,
}
