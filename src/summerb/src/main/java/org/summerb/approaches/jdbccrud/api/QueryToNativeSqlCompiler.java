package org.summerb.approaches.jdbccrud.api;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.approaches.jdbccrud.api.query.Query;

/**
 * 
 * @author sergey.karpushin
 *
 */
public interface QueryToNativeSqlCompiler {
	String buildWhereClauseAndPopulateParams(Query query, MapSqlParameterSource params);
}
