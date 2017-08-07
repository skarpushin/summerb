package org.summerb.approaches.jdbccrud.common;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * This class is introduced to return deprecated and removed methods like
 * queryForInt and queryForLong to {@link NamedParameterJdbcTemplate}
 * 
 * @author sergeyk
 *
 */
public class NamedParameterJdbcTemplateEx extends NamedParameterJdbcTemplate {
	public NamedParameterJdbcTemplateEx(JdbcOperations classicJdbcTemplate) {
		super(classicJdbcTemplate);
	}

	public NamedParameterJdbcTemplateEx(DataSource dataSource) {
		super(dataSource);
	}

	public int queryForInt(String sql, SqlParameterSource params) {
		Integer ret = queryForObject(sql, params, Integer.class);
		return ret == null ? 0 : ret;
	}

	public int queryForInt(String sql, Map<String, Object> hashMap) {
		Integer ret = queryForObject(sql, hashMap, Integer.class);
		return ret == null ? 0 : ret;
	}

	public long queryForLong(String sql, SqlParameterSource params) {
		Long ret = queryForObject(sql, params, Long.class);
		return ret == null ? 0 : ret;
	}

	public long queryForLong(String sql, Map<String, Object> hashMap) {
		Long ret = queryForObject(sql, hashMap, Long.class);
		return ret == null ? 0 : ret;
	}

}
