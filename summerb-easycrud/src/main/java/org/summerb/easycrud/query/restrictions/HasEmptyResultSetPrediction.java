package org.summerb.easycrud.query.restrictions;

// TBD: Make Query smarter -- if one of the in() conditions received empty collection => detect
//  this and gracefully return empty collection instead of throwing exception. This will simplify
//  consumer code

public interface HasEmptyResultSetPrediction {
  boolean isPredictsEmptyResult();
}
