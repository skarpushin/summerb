package org.summerb.microservices.properties.impl.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.summerb.approaches.jdbccrud.common.DaoBase;
import org.summerb.microservices.properties.impl.dao.PropertyDao;
import org.summerb.microservices.properties.impl.dto.NamedIdProperty;

public class PropertyDaoImpl extends DaoBase implements PropertyDao, InitializingBean {
	private String tableName = "props_values";
	private String sqlPutProperty;
	private String sqlFindSingleSubjectProperty;
	private String sqlFindAllSubjectProperty;
	private String sqlDeleAllSubjectProperties;

	private RowMapper<String> propertyValueRowMapper = new RowMapper<String>() {
		@Override
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			String value = rs.getString(VALUE_FIELD_NAME);
			// TOOD: Can somebody explain why we would copu this string?
			return value == null ? null : new String(value);
		}
	};

	private RowMapper<NamedIdProperty> namedPropertyRowMapper = new RowMapper<NamedIdProperty>() {
		@Override
		public NamedIdProperty mapRow(ResultSet rs, int rowNum) throws SQLException {
			String value = rs.getString(VALUE_FIELD_NAME);
			// TOOD: Can somebody explain why we would copu this string?
			return new NamedIdProperty(rs.getLong("name_id"), value == null ? null : new String(value));
		}
	};

	@Override
	public void afterPropertiesSet() throws Exception {
		sqlPutProperty = String.format(
				"INSERT INTO %s (app_id, domain_id, subject_id, name_id, value) VALUES (:app_id, :domain_id, :subject_id, :name_id, :value)  ON DUPLICATE KEY UPDATE value = :value",
				tableName);

		sqlFindSingleSubjectProperty = String.format(
				"SELECT name_id, value FROM %s WHERE app_id = :app_id AND domain_id = :domain_id AND subject_id = :subject_id AND name_id = :name_id",
				tableName);

		sqlFindAllSubjectProperty = String.format(
				"SELECT name_id, value FROM %s WHERE app_id = :app_id AND domain_id = :domain_id AND subject_id = :subject_id",
				tableName);

		sqlDeleAllSubjectProperties = String.format(
				"DELETE FROM %s WHERE app_id = :app_id AND domain_id = :domain_id AND subject_id = :subject_id",
				tableName);
	}

	@Override
	public void putProperty(long appId, long domainId, String subjectId, long propertyNameId, String propertyValue) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("app_id", appId);
		params.put("domain_id", domainId);
		params.put("subject_id", subjectId);
		params.put("name_id", propertyNameId);
		params.put(VALUE_FIELD_NAME, propertyValue);

		jdbc.update(sqlPutProperty, params);
	}

	@Override
	public String findSubjectProperty(long appId, long domainId, String subjectId, long propertyNameId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("app_id", appId);
		params.put("domain_id", domainId);
		params.put("subject_id", subjectId);
		params.put("name_id", propertyNameId);

		try {
			return jdbc.queryForObject(sqlFindSingleSubjectProperty, params, propertyValueRowMapper);
		} catch (EmptyResultDataAccessException er) {
			// not found
			return null;
		}
	}

	@Override
	public List<NamedIdProperty> findSubjectProperties(long appId, long domainId, String subjectId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("app_id", appId);
		params.put("domain_id", domainId);
		params.put("subject_id", subjectId);

		return jdbc.query(sqlFindAllSubjectProperty, params, namedPropertyRowMapper);
	}

	@Override
	public void deleteSubjectProperties(long appId, long domainId, String subjectId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("app_id", appId);
		params.put("domain_id", domainId);
		params.put("subject_id", subjectId);

		jdbc.update(sqlDeleAllSubjectProperties, params);
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
