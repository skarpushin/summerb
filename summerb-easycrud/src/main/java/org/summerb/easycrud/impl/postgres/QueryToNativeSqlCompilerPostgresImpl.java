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
package org.summerb.easycrud.impl.postgres;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.QueryToNativeSqlCompiler;
import org.summerb.easycrud.api.query.restrictions.StringContainsRestriction;
import org.summerb.easycrud.api.query.restrictions.StringEqRestriction;
import org.summerb.easycrud.api.query.restrictions.StringOneOfRestriction;
import org.summerb.easycrud.impl.mysql.ConditionConverter;
import org.summerb.easycrud.impl.mysql.QueryToNativeSqlCompilerMySqlImpl;

/**
 * MySQL specific impl of {@link QueryToNativeSqlCompiler}
 * 
 * WARNING!!! It is suggested to have an index "lower(string_field)" for text
 * fields which will be searched
 * 
 * @author sergey.karpushin
 *
 */
public class QueryToNativeSqlCompilerPostgresImpl extends QueryToNativeSqlCompilerMySqlImpl {
	public QueryToNativeSqlCompilerPostgresImpl() {
		super();
		converters.put(StringEqRestriction.class, stringEqRestriction);
		converters.put(StringContainsRestriction.class, stringContainsRestriction);
		converters.put(StringOneOfRestriction.class, stringOneOfRestriction);
	}

	private ConditionConverter<StringEqRestriction> stringEqRestriction = new ConditionConverter<StringEqRestriction>() {
		@Override
		public String convert(StringEqRestriction r, MapSqlParameterSource params, AtomicInteger paramIdx,
				String underscoredFieldName) {
			String pn = pname(paramIdx);
			params.addValue(pn, r.getValue().toLowerCase());
			return String.format("lower(%s) %s :%s", underscoredFieldName, r.isNegative() ? " != " : " = ", pn);
		}
	};

	private ConditionConverter<StringContainsRestriction> stringContainsRestriction = new ConditionConverter<StringContainsRestriction>() {
		@Override
		public String convert(StringContainsRestriction r, MapSqlParameterSource params, AtomicInteger paramIdx,
				String underscoredFieldName) {
			String pn = pname(paramIdx);
			params.addValue(pn, "%" + r.getValue() + "%");
			String ret = underscoredFieldName + (r.isNegative() ? " NOT ILIKE :" : " ILIKE :") + pn;
			return ret;
		}
	};

	private ConditionConverter<StringOneOfRestriction> stringOneOfRestriction = new ConditionConverter<StringOneOfRestriction>() {
		@Override
		public String convert(StringOneOfRestriction r, MapSqlParameterSource params, AtomicInteger paramIdx,
				String underscoredFieldName) {
			String pn = pname(paramIdx);
			params.addValue(pn, r.getValues().stream().map(x -> x.toLowerCase()).collect(Collectors.toList()));
			return String.format("lower(%s) %s (:%s)", underscoredFieldName, r.isNegative() ? " NOT IN " : " IN ", pn);
		}
	};

}
