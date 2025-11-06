package org.summerb.easycrud.sql_builder.model;

import java.util.List;
import org.summerb.easycrud.query.Query;

public class ColumnsSelection {
  protected Query<?, ?> query;
  protected boolean isWildcardAdded;
  protected List<SelectedColumn> columns;

  public boolean isWildcardAdded() {
    return isWildcardAdded;
  }

  public void setWildcardAdded(boolean wildcardAdded) {
    isWildcardAdded = wildcardAdded;
  }

  public Query<?, ?> getQuery() {
    return query;
  }

  public void setQuery(Query<?, ?> query) {
    this.query = query;
  }

  public List<SelectedColumn> getColumns() {
    return columns;
  }

  public void setColumns(List<SelectedColumn> columns) {
    this.columns = columns;
  }
}
