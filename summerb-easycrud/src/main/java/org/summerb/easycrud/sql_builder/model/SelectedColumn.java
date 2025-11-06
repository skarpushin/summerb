package org.summerb.easycrud.sql_builder.model;

public class SelectedColumn {
  /** Original field name from the Row POJO */
  protected String fieldName;

  /** Column name, as it is called in table schema, without aliases */
  protected String columnName;

  /** Column label, as it is going to appear in {@link java.sql.ResultSet} */
  protected String columnLabel;

  public SelectedColumn(String fieldName, String columnName, String columnLabel) {
    this.fieldName = fieldName;
    this.columnName = columnName;
    this.columnLabel = columnLabel;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getColumnLabel() {
    return columnLabel;
  }

  public void setColumnLabel(String columnLabel) {
    this.columnLabel = columnLabel;
  }
}
