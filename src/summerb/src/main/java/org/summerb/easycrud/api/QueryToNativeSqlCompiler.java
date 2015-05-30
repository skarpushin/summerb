package org.summerb.easycrud.api;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.query.Query;

/**
 * 
 * @author sergey.karpushin
 *
 */public interface QueryToNativeSqlCompiler {
	String buildWhereClauseAndPopulateParams(Query query, MapSqlParameterSource params);
}
