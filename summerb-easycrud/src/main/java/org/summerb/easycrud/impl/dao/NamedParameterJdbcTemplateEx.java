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
package org.summerb.easycrud.impl.dao;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * This class is introduced to return deprecated and removed methods like queryForInt and
 * queryForLong to {@link NamedParameterJdbcTemplate}
 *
 * @author sergeyk
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
