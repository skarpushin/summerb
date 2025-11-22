package org.summerb.easycrud.join_query;

public enum ConditionsLocation {
  /** Conditions for this query, if any, will be placed in JOIN clause */
  JOIN,

  /** Conditions for this query, if any, will be placed in WHERE clause */
  WHERE,

  /** Special case -- when query conditions are added to WHERE in a form of EXISTS or NOT EXISTS */
  WHERE_EXISTS
}
