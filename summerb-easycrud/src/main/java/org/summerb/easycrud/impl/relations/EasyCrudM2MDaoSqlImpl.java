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
package org.summerb.easycrud.impl.relations;

import javax.sql.DataSource;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.api.row.relations.ManyToManyRow;
import org.summerb.easycrud.impl.dao.mysql.EasyCrudDaoSqlImpl;

public class EasyCrudM2MDaoSqlImpl<T1Id, T1Dto extends HasId<T1Id>, T2Id, T2Dto extends HasId<T2Id>>
    extends EasyCrudDaoSqlImpl<Long, ManyToManyRow<T1Id, T2Id>> {

  @SuppressWarnings("deprecation")
  public EasyCrudM2MDaoSqlImpl(DataSource dataSource, String tableName) {
    super();
    this.dataSource = dataSource;
    this.tableName = tableName;
    this.rowClass = determineRowClass();
  }

  public EasyCrudM2MDaoSqlImpl(
      String tableName, Class<ManyToManyRow<T1Id, T2Id>> rowClass, DataSource dataSource) {
    super(dataSource, tableName, rowClass);
  }

  @SuppressWarnings("unchecked")
  protected Class<ManyToManyRow<T1Id, T2Id>> determineRowClass() {
    ManyToManyRow<T1Id, T2Id> example = new ManyToManyRow<>();
    return (Class<ManyToManyRow<T1Id, T2Id>>) example.getClass();
  }
}
