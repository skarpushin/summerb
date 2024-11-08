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
package org.summerb.properties.impl.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.summerb.easycrud.api.DaoExceptionTranslator;
import org.summerb.easycrud.impl.dao.TableDaoBase;
import org.summerb.easycrud.impl.dao.mysql.DaoExceptionTranslatorMySqlImpl;
import org.summerb.properties.impl.dao.PropertyDao;
import org.summerb.properties.impl.dto.NamedIdProperty;

public class PropertyDaoImpl extends TableDaoBase implements PropertyDao, InitializingBean {
  protected String sqlPutProperty;
  protected String sqlFindSingleSubjectProperty;
  protected String sqlFindAllSubjectProperty;
  protected String sqlDeleAllSubjectProperties;

  public PropertyDaoImpl(DataSource dataSource, String tableName /*"props_values"*/) {
    super(dataSource, tableName);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();
    buildSqlStatements(tableName);
  }

  protected void buildSqlStatements(String tableName) {
    sqlPutProperty =
        String.format(
            "INSERT INTO %s (app_id, domain_id, subject_id, name_id, value) VALUES (:app_id, :domain_id, :subject_id, :name_id, :value)  ON DUPLICATE KEY UPDATE value = :value",
            tableName);

    sqlFindSingleSubjectProperty =
        String.format(
            "SELECT name_id, value FROM %s WHERE app_id = :app_id AND domain_id = :domain_id AND subject_id = :subject_id AND name_id = :name_id",
            tableName);

    sqlFindAllSubjectProperty =
        String.format(
            "SELECT name_id, value FROM %s WHERE app_id = :app_id AND domain_id = :domain_id AND subject_id = :subject_id",
            tableName);

    sqlDeleAllSubjectProperties =
        String.format(
            "DELETE FROM %s WHERE app_id = :app_id AND domain_id = :domain_id AND subject_id = :subject_id",
            tableName);
  }

  protected DaoExceptionTranslator daoExceptionTranslator = new DaoExceptionTranslatorMySqlImpl();

  protected RowMapper<String> propertyValueRowMapper =
      new RowMapper<>() {
        @Override
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
          String value = rs.getString(VALUE_FIELD_NAME);
          // TOOD: Can somebody explain why we would copy this string?
          return value == null ? null : new String(value);
        }
      };

  protected RowMapper<NamedIdProperty> namedPropertyRowMapper =
      new RowMapper<>() {
        @Override
        public NamedIdProperty mapRow(ResultSet rs, int rowNum) throws SQLException {
          String value = rs.getString(VALUE_FIELD_NAME);
          // TOOD: Can somebody explain why we would copy this string?
          return new NamedIdProperty(
              rs.getLong("name_id"), value == null ? null : new String(value));
        }
      };

  @Override
  public void putProperty(
      long appId, long domainId, String subjectId, long propertyNameId, String propertyValue) {
    Map<String, Object> params = new HashMap<>();
    params.put("app_id", appId);
    params.put("domain_id", domainId);
    params.put("subject_id", subjectId);
    params.put("name_id", propertyNameId);
    params.put(VALUE_FIELD_NAME, propertyValue);

    try {
      jdbc.update(sqlPutProperty, params);
    } catch (Exception e) {
      daoExceptionTranslator.translateAndThrowIfApplicableUnchecked(e);
      throw e;
    }
  }

  @Override
  public String findSubjectProperty(
      long appId, long domainId, String subjectId, long propertyNameId) {
    Map<String, Object> params = new HashMap<>();
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
    Map<String, Object> params = new HashMap<>();
    params.put("app_id", appId);
    params.put("domain_id", domainId);
    params.put("subject_id", subjectId);

    return jdbc.query(sqlFindAllSubjectProperty, params, namedPropertyRowMapper);
  }

  @Override
  public void deleteSubjectProperties(long appId, long domainId, String subjectId) {
    Map<String, Object> params = new HashMap<>();
    params.put("app_id", appId);
    params.put("domain_id", domainId);
    params.put("subject_id", subjectId);

    try {
      jdbc.update(sqlDeleAllSubjectProperties, params);
    } catch (Exception e) {
      daoExceptionTranslator.translateAndThrowIfApplicableUnchecked(e);
      throw e;
    }
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public DaoExceptionTranslator getDaoExceptionToFveTranslator() {
    return daoExceptionTranslator;
  }

  public void setDaoExceptionToFveTranslator(DaoExceptionTranslator daoExceptionTranslator) {
    this.daoExceptionTranslator = daoExceptionTranslator;
  }
}
