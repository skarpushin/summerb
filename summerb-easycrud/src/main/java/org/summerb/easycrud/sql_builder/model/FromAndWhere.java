package org.summerb.easycrud.sql_builder.model;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class FromAndWhere {
  protected String sql;
  protected MapSqlParameterSource params;

  public FromAndWhere(String sql, MapSqlParameterSource params) {
    this.sql = sql;
    this.params = params;
  }

  public MapSqlParameterSource getParams() {
    return params;
  }

  public void setParams(MapSqlParameterSource params) {
    this.params = params;
  }

  public String getSql() {
    return sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }
}
