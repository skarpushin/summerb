/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.RowMapper;
import org.summerb.easycrud.common.DaoBase;
import org.summerb.users.impl.dao.PermissionDao;

public class PermissionDaoImpl extends DaoBase implements InitializingBean, PermissionDao {
	private String tableName = "users_permissions";
	private String sqlGetUserPermissionsForSubject;
	private String sqlGetSubjectsUserHasPermissionFor;
	private String sqlGetSubjectsUserHasPermissionForFiltered;
	private String sqlInsertPermission;
	private String sqlDeletePermission;
	private String sqlDeleteUserPermission;
	private String sqlSubjectPermissions;
	private String sqlGetSubjectUsersAndPermissions;

	private RowMapper<String> permissionKeyRowMapper = new RowMapper<String>() {
		@Override
		public String mapRow(ResultSet rs, int idx) throws SQLException {
			return rs.getString("permission_key");
		}
	};

	private RowMapper<String> subjectIdRowMapper = new RowMapper<String>() {
		@Override
		public String mapRow(ResultSet rs, int idx) throws SQLException {
			return rs.getString("subject_id");
		}
	};

	@Override
	public void afterPropertiesSet() throws Exception {
		sqlGetUserPermissionsForSubject = String.format(
				"SELECT permission_key FROM %s WHERE domain_name = :domainName AND subject_id = :subjectId AND user_uuid = :userUuid",
				getTableName());

		sqlGetSubjectsUserHasPermissionFor = String.format(
				"SELECT subject_id FROM %s WHERE domain_name = :domainName AND user_uuid = :userUuid", getTableName());

		sqlGetSubjectsUserHasPermissionForFiltered = String.format(
				"SELECT subject_id FROM %s WHERE domain_name = :domainName AND user_uuid = :userUuid AND permission_key = :permissionKey",
				getTableName());

		sqlGetSubjectUsersAndPermissions = String.format(
				"SELECT user_uuid, permission_key FROM %s WHERE domain_name = :domainName AND subject_id = :subjectId",
				getTableName());

		sqlInsertPermission = String.format(
				"INSERT IGNORE INTO %s (domain_name, subject_id, user_uuid, permission_key) VALUES (:domainName, :subjectId, :userUuid, :permissionKey)",
				tableName);

		sqlDeletePermission = String.format(
				"DELETE FROM %s WHERE domain_name = :domainName AND subject_id = :subjectId AND user_uuid = :userUuid AND permission_key = :permissionKey",
				tableName);

		sqlDeleteUserPermission = String
				.format("DELETE FROM %s WHERE domain_name = :domainName AND user_uuid = :userUuid", tableName);

		sqlSubjectPermissions = String
				.format("DELETE FROM %s WHERE domain_name = :domainName AND subject_id = :subjectId", tableName);
	}

	@Override
	public void grantPermission(String domainName, String userUuid, String subjectId, String permissionKey) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("domainName", domainName);
		paramMap.put("subjectId", subjectId);
		paramMap.put("userUuid", userUuid);
		paramMap.put("permissionKey", permissionKey);
		jdbc.update(sqlInsertPermission, paramMap);
	}

	@Override
	public void revokePermission(String domainName, String userUuid, String subjectId, String permissionKey) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("domainName", domainName);
		paramMap.put("subjectId", subjectId);
		paramMap.put("userUuid", userUuid);
		paramMap.put("permissionKey", permissionKey);
		jdbc.update(sqlDeletePermission, paramMap);
	}

	@Override
	public void revokeUserPermissions(String domainName, String userUuid) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("domainName", domainName);
		paramMap.put("userUuid", userUuid);
		jdbc.update(sqlDeleteUserPermission, paramMap);
	}

	@Override
	public void clearSubjectPermissions(String domainName, String subjectId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("domainName", domainName);
		paramMap.put("subjectId", subjectId);
		jdbc.update(sqlSubjectPermissions, paramMap);
	}

	@Override
	public List<String> getUserPermissionsForSubject(String domainName, String userUuid, String subjectId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("domainName", domainName);
		paramMap.put("userUuid", userUuid);
		paramMap.put("subjectId", subjectId);
		return jdbc.query(sqlGetUserPermissionsForSubject, paramMap, permissionKeyRowMapper);
	}

	@Override
	public List<String> getSubjectsUserHasPermissionFor(String domainName, String userUuid) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("domainName", domainName);
		paramMap.put("userUuid", userUuid);
		return jdbc.query(sqlGetSubjectsUserHasPermissionFor, paramMap, subjectIdRowMapper);
	}

	@Override
	public List<String> getSubjectsUserHasPermissionForFiltered(String domainName, String userUuid,
			String requiredPermission) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("domainName", domainName);
		paramMap.put("userUuid", userUuid);
		paramMap.put("permissionKey", requiredPermission);
		return jdbc.query(sqlGetSubjectsUserHasPermissionForFiltered, paramMap, subjectIdRowMapper);
	}

	@Override
	public Map<String, List<String>> getUsersAndTheirPermissionsForSubject(String domainName, String subjectId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("domainName", domainName);
		paramMap.put("subjectId", subjectId);

		List<Map<String, Object>> results = jdbc.queryForList(sqlGetSubjectUsersAndPermissions, paramMap);
		Map<String, List<String>> ret = new HashMap<String, List<String>>();
		for (Map<String, Object> row : results) {
			String userUuid = (String) row.get("user_uuid");
			String permissionKey = (String) row.get("permission_key");

			List<String> userMap = ret.get(userUuid);
			if (userMap == null) {
				ret.put(userUuid, userMap = new ArrayList<String>());
			}
			userMap.add(permissionKey);
		}
		return ret;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
