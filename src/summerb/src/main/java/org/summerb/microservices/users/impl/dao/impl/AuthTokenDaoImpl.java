package org.summerb.microservices.users.impl.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.summerb.approaches.jdbccrud.common.DaoBase;
import org.summerb.microservices.users.api.dto.AuthToken;
import org.summerb.microservices.users.impl.dao.AuthTokenDao;

public class AuthTokenDaoImpl extends DaoBase implements InitializingBean, AuthTokenDao {
	private static final String PARAM_LAST_VERIFIED_AT = "lastVerifiedAt";
	private static final String PARAM_AUTH_TOKEN_UUID = "authTokenUuid";
	private static final String PARAM_TOKEN_VALUE = "tokenValue";
	private static final String PARAM_USER_UUID = "userUuid";

	private SimpleJdbcInsert jdbcInsert;
	private String tableName = "users_auth_tokens";
	private BeanPropertyRowMapper<AuthToken> rowMapper;

	private String sqlSelectTokenByUuid;
	private String sqlUpdateToken;
	private String sqlDeleteTokenByUuid;
	private String sqlSearchUserTokens;

	@Override
	public void afterPropertiesSet() throws Exception {
		rowMapper = new BeanPropertyRowMapper<AuthToken>(AuthToken.class);
		jdbcInsert = new SimpleJdbcInsert(getDataSource()).withTableName(tableName);

		sqlSelectTokenByUuid = String.format("SELECT * FROM %s u WHERE u.uuid = :authTokenUuid", tableName);
		sqlUpdateToken = String.format(
				"UPDATE %s SET last_verified_at = :lastVerifiedAt, token_value = :tokenValue WHERE uuid = :authTokenUuid AND last_verified_at < :lastVerifiedAt",
				tableName);
		sqlDeleteTokenByUuid = String.format("DELETE FROM %s WHERE uuid = :authTokenUuid", tableName);

		sqlSearchUserTokens = String.format("SELECT u.* FROM %s u WHERE u.user_uuid = :userUuid", tableName);
	}

	@Override
	public void createAuthToken(AuthToken authToken) {
		BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(authToken);
		jdbcInsert.execute(params);
	}

	@Override
	public AuthToken findAuthTokenByUuid(String authTokenUuid) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(PARAM_AUTH_TOKEN_UUID, authTokenUuid);
		List<AuthToken> ret = jdbc.query(sqlSelectTokenByUuid, paramMap, rowMapper);
		if (ret.size() == 1) {
			return ret.get(0);
		}
		return null;
	}

	@Override
	public void updateToken(String authTokenUuid, long now, String newTokenValue) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(PARAM_AUTH_TOKEN_UUID, authTokenUuid);
		paramMap.put(PARAM_LAST_VERIFIED_AT, now);
		paramMap.put(PARAM_TOKEN_VALUE, newTokenValue);
		jdbc.update(sqlUpdateToken, paramMap);
	}

	@Override
	public void deleteAuthToken(String authTokenUuid) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(PARAM_AUTH_TOKEN_UUID, authTokenUuid);
		jdbc.update(sqlDeleteTokenByUuid, paramMap);
	}

	@Override
	public List<AuthToken> findAuthTokensByUser(String userUuid) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(PARAM_USER_UUID, userUuid);
		return jdbc.query(sqlSearchUserTokens, paramMap, rowMapper);
	}

}
