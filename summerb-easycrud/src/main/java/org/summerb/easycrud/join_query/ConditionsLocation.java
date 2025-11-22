package org.summerb.easycrud.join_query;

public enum ConditionsLocation {
  /** Conditions for this query, if any, will be placed in JOIN clause */
  JOIN,

  /** Conditions for this query, if any, will be placed in WHERE clause */
  WHERE,
}
