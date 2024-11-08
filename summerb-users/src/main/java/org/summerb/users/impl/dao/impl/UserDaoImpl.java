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

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.easycrud.impl.dao.TableDaoBase;
import org.summerb.users.api.dto.User;
import org.summerb.users.impl.dao.UserDao;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;

/**
 * Expects following indexes: uuid (unique), email (unique), displayName
 *
 * @author skarpushin
 */
public class UserDaoImpl extends TableDaoBase implements InitializingBean, UserDao {
  protected static final String PARAM_MAX = "max";
  protected static final String PARAM_OFFSET = "offset";
  protected static final String PARAM_DISPLAY_NAME_AS_IS = "displayNameAsIs";
  protected static final String PARAM_DISPLAY_NAME = "displayName";
  protected static final String PARAM_USER_UUID = "userUuid";
  protected static final String PARAM_USER_EMAIL = "userEmail";

  protected SimpleJdbcInsert jdbcInsert;
  protected BeanPropertyRowMapper<User> rowMapper;
  protected String sqlSelectUserByUuid;
  protected String sqlSelectUserByEmail;
  protected String sqlDeleteUserByUuid;
  protected String sqlUpdateUser;
  protected String sqlSearchUsersByDisplayName;
  protected String sqlSearchUsersByDisplayNameGetCount;

  /**
   * @param dataSource dataSource
   * @param tableName i.e. "users"
   */
  public UserDaoImpl(DataSource dataSource, String tableName) {
    super(dataSource, tableName);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();

    rowMapper = new BeanPropertyRowMapper<>(User.class);
    jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(tableName);

    sqlSelectUserByUuid = String.format("SELECT * FROM %s u WHERE u.uuid = :userUuid", tableName);
    sqlSelectUserByEmail =
        String.format("SELECT * FROM %s u WHERE u.email = :userEmail", tableName);
    sqlDeleteUserByUuid = String.format("DELETE FROM %s WHERE uuid = :userUuid", tableName);
    sqlUpdateUser =
        String.format(
            "UPDATE %s SET display_name = :displayName, email = :email, time_zone = :timeZone, locale = :locale, is_blocked = :isBlocked, integration_data = :integrationData WHERE uuid = :uuid",
            tableName);

    sqlSearchUsersByDisplayName =
        String.format(
            "SELECT SQL_CALC_FOUND_ROWS u.*, INSTR(u.display_name, :displayNameAsIs) as ddd FROM %s u WHERE u.display_name LIKE :displayName ORDER BY ddd ASC, u.display_name LIMIT :offset,:max",
            tableName);
    sqlSearchUsersByDisplayNameGetCount = "SELECT FOUND_ROWS()";
  }

  @Override
  public void createUser(User user) throws DuplicateKeyException {
    BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(user);
    jdbcInsert.execute(params);
  }

  @Override
  public User findUserByUuid(String userUuid) {
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put(PARAM_USER_UUID, userUuid);
    List<User> ret = jdbc.query(sqlSelectUserByUuid, paramMap, rowMapper);
    if (ret.size() == 1) {
      return ret.get(0);
    }
    return null;
  }

  @Override
  public User findUserByEmail(String userEmail) {
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put(PARAM_USER_EMAIL, userEmail);
    List<User> ret = jdbc.query(sqlSelectUserByEmail, paramMap, rowMapper);
    if (ret.size() == 1) {
      return ret.get(0);
    }
    return null;
  }

  @Override
  public boolean updateUser(User user) throws DuplicateKeyException {
    BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(user);
    return jdbc.update(sqlUpdateUser, params) == 1;
  }

  @Override
  public boolean deleteUser(String userUuid) {
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put(PARAM_USER_UUID, userUuid);
    return jdbc.update(sqlDeleteUserByUuid, paramMap) == 1;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public PaginatedList<User> findUserByDisplayNamePartial(
      String displayNamePartial, PagerParams pagerParams) {
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put(PARAM_DISPLAY_NAME, "%" + displayNamePartial + "%");
    paramMap.put(PARAM_DISPLAY_NAME_AS_IS, displayNamePartial);
    paramMap.put(PARAM_OFFSET, pagerParams.getOffset());
    paramMap.put(PARAM_MAX, pagerParams.getMax());
    List<User> results = jdbc.query(sqlSearchUsersByDisplayName, paramMap, rowMapper);
    int totalResultsCount = jdbc.queryForInt(sqlSearchUsersByDisplayNameGetCount, new HashMap<>());
    return new PaginatedList<>(pagerParams, results, totalResultsCount);
  }

  public String getTableName() {
    return tableName;
  }
}
