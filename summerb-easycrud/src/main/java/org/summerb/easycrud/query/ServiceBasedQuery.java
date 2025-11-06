package org.summerb.easycrud.query;

import org.summerb.easycrud.EasyCrudService;
import org.summerb.easycrud.row.HasId;

public interface ServiceBasedQuery<
    TId, TRow extends HasId<TId>, TService extends EasyCrudService<TId, TRow>> {

  TService getService();
}
