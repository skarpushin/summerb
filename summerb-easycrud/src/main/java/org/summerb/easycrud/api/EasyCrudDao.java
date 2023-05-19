/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.easycrud.api;

import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.easycrud.api.query.OrderBy;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.impl.dao.mysql.EasyCrudDaoMySqlImpl;

/**
 * Abstraction for DAO layer. Intended to be used by impl of {@link EasyCrudService}.
 *
 * <p>In case you're using MySQL as a data source you can easily impl DAO by simply extending
 * .{@link EasyCrudDaoMySqlImpl}
 *
 * @author sergey.karpushin
 */
public interface EasyCrudDao<TId, TRow extends HasId<TId>> {
  void create(TRow row);

  TRow findById(TId id);

  TRow findOneByQuery(Query query);

  int delete(TId id);

  int delete(TId id, long modifiedAt);

  int update(TRow row);

  int deleteByQuery(Query query);

  PaginatedList<TRow> query(PagerParams pagerParams, Query optionalQuery, OrderBy... orderBy);
}
