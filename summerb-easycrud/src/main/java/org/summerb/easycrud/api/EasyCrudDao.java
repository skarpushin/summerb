/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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

import org.summerb.easycrud.api.dto.OrderBy;
import org.summerb.easycrud.api.query.QueryConditions;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.impl.dao.mysql.EasyCrudDaoSqlImpl;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;

/**
 * Abstraction for DAO layer. Intended to be used by impl of {@link EasyCrudService}.
 *
 * <p>In case you're using MySQL as a data source you can easily impl DAO by simply extending
 * .{@link EasyCrudDaoSqlImpl}
 *
 * @author sergey.karpushin
 */
public interface EasyCrudDao<TId, TRow extends HasId<TId>> {
  void create(TRow row);

  TRow findById(TId id);

  TRow findOneByQuery(QueryConditions query);

  int delete(TId id);

  int delete(TId id, long modifiedAt);

  int update(TRow row);

  int deleteByQuery(QueryConditions query);

  PaginatedList<TRow> query(
      PagerParams pagerParams, QueryConditions optionalQuery, OrderBy... orderBy);
}
