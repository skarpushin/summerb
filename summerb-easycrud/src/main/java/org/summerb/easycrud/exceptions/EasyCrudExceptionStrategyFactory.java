package org.summerb.easycrud.exceptions;

import org.summerb.easycrud.row.HasId;

/**
 * This factory can be overridden if you want to customize exception handling for all row services
 */
public interface EasyCrudExceptionStrategyFactory {
  <TId extends Comparable<TId>, TRow extends HasId<TId>>
      EasyCrudExceptionStrategy<TId, TRow> create(String rowMessageCode);
}
