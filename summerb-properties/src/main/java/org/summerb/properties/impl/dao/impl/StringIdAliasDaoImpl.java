/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.util.CollectionUtils;
import org.summerb.easycrud.dao.TableDaoBase;
import org.summerb.properties.impl.dao.AliasEntry;
import org.summerb.properties.impl.dao.StringIdAliasDao;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;

public class StringIdAliasDaoImpl extends TableDaoBase implements StringIdAliasDao {
  protected static final String PARAM_ALIAS = "alias";
  protected static final String PARAM_ALIAS_NAME = "alias_name";
  protected static final String PARAM_MAX = "max";
  protected static final String PARAM_OFFSET = "offset";

  protected SimpleJdbcInsert jdbcInsert;
  protected String sqlFindAliasByName;
  protected String sqlFindAliasesPaged;
  protected String sqlFindAllAliases;
  protected String sqlLastStatementCount;
  protected String sqlFindNameByAlias;

  public StringIdAliasDaoImpl(DataSource dataSource, String tableName) {
    super(dataSource, tableName);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();
    buildSqlStatements();
  }

  protected void buildSqlStatements() {
    jdbcInsert =
        new SimpleJdbcInsert(dataSource).withTableName(tableName).usingGeneratedKeyColumns("alias");

    sqlFindAliasByName =
        String.format("SELECT alias FROM %s WHERE alias_name = :alias_name", tableName);
    sqlFindNameByAlias = String.format("SELECT alias_name FROM %s WHERE alias = :alias", tableName);

    sqlFindAllAliases = String.format("SELECT alias_name, alias FROM %s", tableName);

    sqlFindAliasesPaged =
        String.format(
            "SELECT SQL_CALC_FOUND_ROWS alias_name, alias FROM %s ORDER BY alias ASC LIMIT :max OFFSET :offset",
            tableName);
    sqlLastStatementCount = "SELECT FOUND_ROWS()";
  }

  @Override
  public long createAliasFor(String str) {
    Number key = jdbcInsert.executeAndReturnKey(getParamForAliasName(str));
    return key.longValue();
  }

  protected Map<String, Object> getParamForAliasName(String str) {
    Map<String, Object> paramMap = new HashMap<>();
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

  protected RowMapper<AliasEntry> rowMapper =
      new RowMapper<>() {
        @Override
        public AliasEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
          return new AliasEntry(rs.getString(PARAM_ALIAS_NAME), rs.getLong(PARAM_ALIAS));
        }
      };

  @Override
  public PaginatedList<AliasEntry> loadAliasesPaged(PagerParams pagerParams) {
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put(PARAM_OFFSET, pagerParams.getOffset());
    paramMap.put(PARAM_MAX, pagerParams.getMax());

    List<AliasEntry> results = jdbc.query(sqlFindAliasesPaged, paramMap, rowMapper);
    int totalResultsCount = jdbc.queryForInt(sqlLastStatementCount, new HashMap<>());

    return new PaginatedList<>(pagerParams, results, totalResultsCount);
  }

  @Override
  public List<AliasEntry> loadAllAliases() {
    return jdbc.query(sqlFindAllAliases, rowMapper);
  }

  // TBD: Method is not tested!!!
  @Override
  public String findAliasName(long alias) {
    Map<String, Object> paramMap = new HashMap<>();
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

  @Override
  public String toString() {
    return super.toString() + ", table=" + tableName;
  }
}
