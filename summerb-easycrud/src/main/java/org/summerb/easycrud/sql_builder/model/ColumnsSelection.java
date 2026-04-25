package org.summerb.easycrud.sql_builder.model;

import java.util.List;
import org.summerb.easycrud.query.Query;

/** Model for column selection in SQL builder. */
public class ColumnsSelection {
  /** Query associated with this selection */
  protected Query<?, ?> query;

  /** Whether wildcard (*) is added */
  protected boolean isWildcardAdded;

  /** List of selected columns */
  protected List<SelectedColumn> columns;

  /**
   * @return true if wildcard is added
   */
  public boolean isWildcardAdded() {
    return isWildcardAdded;
  }

  /**
   * @param wildcardAdded whether wildcard is added
   */
  public void setWildcardAdded(boolean wildcardAdded) {
    isWildcardAdded = wildcardAdded;
  }

  /**
   * @return query associated with this selection
   */
  public Query<?, ?> getQuery() {
    return query;
  }

  /**
   * @param query query associated with this selection
   */
  public void setQuery(Query<?, ?> query) {
    this.query = query;
  }

  /**
   * @return list of selected columns
   */
  public List<SelectedColumn> getColumns() {
    return columns;
  }

  /**
   * @param columns list of selected columns
   */
  public void setColumns(List<SelectedColumn> columns) {
    this.columns = columns;
  }
}
