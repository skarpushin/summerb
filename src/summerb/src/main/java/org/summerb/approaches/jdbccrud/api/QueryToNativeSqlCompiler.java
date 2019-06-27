package org.summerb.approaches.jdbccrud.api;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.jdbccrud.impl.mysql.QueryToNativeSqlCompilerMySqlImpl;

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
