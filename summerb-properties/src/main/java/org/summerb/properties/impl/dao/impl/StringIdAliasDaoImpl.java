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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.easycrud.common.DaoBase;
import org.summerb.properties.impl.dao.AliasEntry;
import org.summerb.properties.impl.dao.StringIdAliasDao;

public class StringIdAliasDaoImpl extends DaoBase implements StringIdAliasDao, InitializingBean {
  private static final String PARAM_ALIAS = "alias";
  private static final String PARAM_ALIAS_NAME = "alias_name";
  private static final String PARAM_MAX = "max";
  private static final String PARAM_OFFSET = "offset";

  private String tableName;
  private SimpleJdbcInsert jdbcInsert;
  private String sqlFindAliasByName;
  private String sqlFindAllAliases;
  private String sqlLastStatementCount;
  private String sqlFindNameByAlias;

  @Override
  public void afterPropertiesSet() throws Exception {
    Assert.hasText(tableName, "Table name must be provided");
    jdbcInsert =
        new SimpleJdbcInsert(getDataSource())
            .withTableName(getTableName())
            .usingGeneratedKeyColumns("alias");

    sqlFindAliasByName =
        String.format("SELECT alias FROM %s WHERE alias_name = :alias_name", tableName);
    sqlFindNameByAlias = String.format("SELECT alias_name FROM %s WHERE alias = :alias", tableName);

    sqlFindAllAliases =
        String.format(
            "SELECT SQL_CALC_FOUND_ROWS alias_name, alias FROM %s ORDER BY alias ASC LIMIT :offset OFFSET :max",
            tableName);
    sqlLastStatementCount = "SELECT FOUND_ROWS()";
  }

  @Override
  public long createAliasFor(String str) {
    Number key = jdbcInsert.executeAndReturnKey(getParamForAliasName(str));
    return key.longValue();
  }

  protected Map<String, Object> getParamForAliasName(String str) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put(PARAM_ALIAS_NAME, str);
    return paramMap;
  }

  @Override
  public Long findAliasFor(String str) {
    List<Long> results =
        jdbc.queryForList(sqlFindAliasByName, getParamForAliasName(str), Long.class);
    if (CollectionUtils.isEmpty(results)) {
      return null;
    }
    return results.get(0);
  }

  private RowMapper<AliasEntry> rowMapper =
      new RowMapper<AliasEntry>() {
        @Override
        public AliasEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
          return new AliasEntry(rs.getString(PARAM_ALIAS_NAME), rs.getLong(PARAM_ALIAS));
        }
      };

  @Override
  public PaginatedList<AliasEntry> loadAllAliases(PagerParams pagerParams) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put(PARAM_OFFSET, pagerParams.getOffset());
    paramMap.put(PARAM_MAX, pagerParams.getMax());

    List<AliasEntry> results = jdbc.query(sqlFindAllAliases, paramMap, rowMapper);
    int totalResultsCount = jdbc.queryForInt(sqlLastStatementCount, new HashMap<String, Object>());

    return new PaginatedList<AliasEntry>(pagerParams, results, totalResultsCount);
  }

  // TBD: Method is not tested!!!
  @Override
  public String findAliasName(long alias) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("alias", alias);

    List<String> results = jdbc.queryForList(sqlFindNameByAlias, paramMap, String.class);
    if (CollectionUtils.isEmpty(results)) {
      return null;
    }

    return results.get(0);
  }

  public String getTableName() {
    return tableName;
  }

  @Required
  public void setTableName(String tableName) {
    this.tableName = tableName;
  }
}
