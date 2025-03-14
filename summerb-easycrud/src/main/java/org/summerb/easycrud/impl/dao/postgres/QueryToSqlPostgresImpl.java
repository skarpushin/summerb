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
package org.summerb.easycrud.impl.dao.postgres;

import com.google.common.base.Preconditions;
import org.summerb.easycrud.api.QueryToSql;
import org.summerb.easycrud.api.query.restrictions.Equals;
import org.summerb.easycrud.api.query.restrictions.In;
import org.summerb.easycrud.api.query.restrictions.Like;
import org.summerb.easycrud.impl.dao.SqlTypeOverrides;
import org.summerb.easycrud.impl.dao.mysql.QueryToSqlMySqlImpl;
import org.summerb.easycrud.impl.dao.postgres.restrictions.EqualsRestrictionToNativeSql;
import org.summerb.easycrud.impl.dao.postgres.restrictions.InRestrictionToNativeSql;
import org.summerb.easycrud.impl.dao.postgres.restrictions.LikeRestrictionToNativeSql;

/**
 * Postgress specific impl of {@link QueryToSql}
 *
 * <p>WARNING!!! It is suggested to have an index "lower(string_field)" for text fields which will
 * be searched
 *
 * @author sergey.karpushin
 */
public class QueryToSqlPostgresImpl extends QueryToSqlMySqlImpl {
  public QueryToSqlPostgresImpl() {
    super();
    converters.put(Equals.class, new EqualsRestrictionToNativeSql());
    converters.put(In.class, new InRestrictionToNativeSql());
    converters.put(Like.class, new LikeRestrictionToNativeSql());
  }

  public QueryToSqlPostgresImpl(SqlTypeOverrides sqlTypeOverrides) {
    this();
    Preconditions.checkNotNull(sqlTypeOverrides, "sqlTypeOverrides required");
    this.sqlTypeOverrides = sqlTypeOverrides;
  }
}
