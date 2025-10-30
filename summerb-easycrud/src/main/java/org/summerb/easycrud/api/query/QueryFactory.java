package org.summerb.easycrud.api.query;

import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.row.HasId;

public interface QueryFactory {
  /**
   * @param <TRow> type of Row (POJO)
   * @param <F> Query type
   * @param service service implementation for this row class
   * @return instance that can be used for both - referring to fields using method references and
   *     string literals
   */
  <TId, TRow extends HasId<TId>, F extends Query<TId, TRow>> F buildFor(
      EasyCrudService<TId, TRow> service);
}
