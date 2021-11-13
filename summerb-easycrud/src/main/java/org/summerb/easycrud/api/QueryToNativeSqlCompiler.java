/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.impl.mysql.QueryToNativeSqlCompilerMySqlImpl;

/**
 * DAO-specific impl that can convert abstracted {@link Query} instance to
 * DAO-specific (native) query.
 * 
 * This interface is specific to Spring's {@link JdbcTemplate} data access. If
 * other type of data source is used than this interface might be irrelevant
 * 
 * @author sergey.karpushin
 *
 * @see QueryToNativeSqlCompilerMySqlImpl
 */
public interface QueryToNativeSqlCompiler {
	String buildWhereClauseAndPopulateParams(Query query, MapSqlParameterSource params);
}
