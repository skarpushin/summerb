package org.summerb.easycrud.sql_builder.model;

import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class QueryData {
  protected List<ColumnsSelection> columnSelections;
  protected String sql;
  protected MapSqlParameterSource params;

  public QueryData(String sql, MapSqlParameterSource params) {
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

  public List<ColumnsSelection> getSelectedColumns() {
    return columnSelections;
  }

  public void setSelectedColumns(List<ColumnsSelection> columnSelections) {
    this.columnSelections = columnSelections;
  }
}
