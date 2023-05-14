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
package org.summerb.users.impl.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.summerb.easycrud.common.DaoBase;
import org.summerb.users.impl.dao.PasswordDao;
import org.summerb.users.impl.dom.Password;

public class PasswordDaoImpl extends DaoBase implements InitializingBean, PasswordDao {
	private static final String PARAM_RESTORATION_TOKEN = "restorationToken";
	private static final String USER_UUID_PARAM = "userUuid";
	private String tableName = "users_passwords";
	private BeanPropertyRowMapper<Password> rowMapper;
	private String sqlPutPassword;
	private String sqlFindPasswordByUserUuid;
	private String sqlSetRestorationToken;

	@Override
	public void afterPropertiesSet() throws Exception {
		rowMapper = new BeanPropertyRowMapper<Password>(Password.class);

		sqlPutPassword = String.format(
				"INSERT INTO %s (user_uuid, password_hash, restoration_token) VALUES (:userUuid, :passwordHash, :restorationToken) ON DUPLICATE KEY UPDATE password_hash = :passwordHash, restoration_token = :restorationToken",
				tableName);

		sqlFindPasswordByUserUuid = String.format("SELECT * FROM %s WHERE user_uuid = :userUuid", tableName);

		sqlSetRestorationToken = String
				.format("UPDATE %s SET restoration_token = :restorationToken WHERE user_uuid = :userUuid", tableName);
	}

	@Override
	public int updateUserPassword(String userUuid, String newPasswordHash) {
		Password pwd = new Password();
		pwd.setUserUuid(userUuid);
		pwd.setPasswordHash(newPasswordHash);
		pwd.setRestorationToken(null);

		BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(pwd);

		return jdbc.update(sqlPutPassword, params);
	}

	@Override
	public Password findPasswordByUserUuid(String userUuid) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(USER_UUID_PARAM, userUuid);
		List<Password> results = jdbc.query(sqlFindPasswordByUserUuid, paramMap, rowMapper);
		if (results.size() == 1) {
			return results.get(0);
		}
		return null;
	}

	@Override
	public int setRestorationToken(String userUuid, String restorationToken) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(USER_UUID_PARAM, userUuid);
		paramMap.put(PARAM_RESTORATION_TOKEN, restorationToken);
		return jdbc.update(sqlSetRestorationToken, paramMap);
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
