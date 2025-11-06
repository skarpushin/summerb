package org.summerb.easycrud.join_query.impl;

import com.google.common.base.Preconditions;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.CollectionUtils;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.sql_builder.model.ColumnsSelection;
import org.summerb.easycrud.sql_builder.model.SelectedColumn;

/**
 * This class is used as a wrapper of original {@link RowMapper} that are used in {@link
 * org.summerb.easycrud.EasyCrudService} to map data to Row classes. It is capable of translating
 * column labels so that the original row mapper thinks that its fields are the only fields that
 * were retrieved from the DB, so there are no conflicts with columns from other tables
 */
@SuppressWarnings("deprecation")
public class RowMappingAdapter implements ResultSet, ResultSetMetaData {
  protected Query<?, ?> query;
  protected ColumnsSelection columnsSelection;
  protected RowMapper<?> rowMapper;

  protected ResultSet rs;
  protected ResultSetMetaData rsmd;
  protected List<String> physicalColumnLabels;

  protected Map<Integer, Integer> logicalColumnIdToPhysical = new HashMap<>();
  protected Map<Integer, String> logicalColumnIdToLogicalName = new HashMap<>();
  protected Map<String, String> logicalColumnNameToPhysicalLabel = new HashMap<>();
  protected Map<String, Integer> logicalNameToLogicalId = new HashMap<>();

  protected Map<Object, HasId<?>> mapIdToRow = new HashMap<>();

  public RowMappingAdapter(ColumnsSelection columnsSelection, RowMapper<?> rowMapper) {
    Preconditions.checkArgument(columnsSelection != null, "columnsSelection is required");
    Preconditions.checkArgument(
        columnsSelection.getQuery() != null, "columnsSelection.query is required");
    Preconditions.checkArgument(
        !CollectionUtils.isEmpty(columnsSelection.getColumns()),
        "columnsSelection.columns is required");
    Preconditions.checkArgument(rowMapper != null, "rowMapper is required");

    this.columnsSelection = columnsSelection;
    this.rowMapper = rowMapper;
    this.query = columnsSelection.getQuery();
  }

  public void setResultSet(ResultSet rs, ResultSetMetaData rsmd, List<String> columnLabels)
      throws SQLException {
    this.rs = rs;
    this.rsmd = rsmd;
    this.physicalColumnLabels = columnLabels;
    buildMappingData();
  }

  protected void buildMappingData() {
    List<SelectedColumn> selectedColumns = columnsSelection.getColumns();
    for (int i = 0; i < selectedColumns.size(); i++) {
      SelectedColumn selectedColumn = selectedColumns.get(i);
      int physicalIndex = physicalColumnLabels.indexOf(selectedColumn.getColumnLabel()) + 1;
      Preconditions.checkState(
          physicalIndex >= 1, "Column not found in resultset: " + selectedColumn.getColumnLabel());
      logicalColumnIdToPhysical.put(i + 1, physicalIndex);
      String logicalColumnName = selectedColumn.getColumnName();
      logicalColumnIdToLogicalName.put(i + 1, logicalColumnName);
      logicalColumnNameToPhysicalLabel.put(logicalColumnName, selectedColumn.getColumnLabel());
      logicalNameToLogicalId.put(logicalColumnName, i + 1);
    }
  }

  public Object mapRow(int rowNum) throws SQLException {
    Object id = getObject("id");
    if (id == null) {
      return null;
    }

    Object existing = mapIdToRow.get(id);
    if (existing != null) {
      return existing;
    }

    Object ret = rowMapper.mapRow(this, rowNum);
    if (ret != null) {
      mapIdToRow.put(id, (HasId<?>) ret);
    }
    return ret;
  }

  protected int toPhysical(int logicalColumnIndex) throws SQLException {
    Integer physicalIndex = logicalColumnIdToPhysical.get(logicalColumnIndex);
    if (physicalIndex == null) {
      throw new SQLException("Invalid column index: " + logicalColumnIndex);
    }
    return physicalIndex;
  }

  protected String toPhysical(String logicalColumnName) throws SQLException {
    String physicalColumnLabel = logicalColumnNameToPhysicalLabel.get(logicalColumnName);
    if (physicalColumnLabel == null) {
      throw new SQLException("Invalid column label: " + logicalColumnName);
    }
    return physicalColumnLabel;
  }

  // ===== ResultSet methods
  @Override
  public ResultSetMetaData getMetaData() {
    return this;
  }

  @Override
  public boolean next() throws SQLException {
    return rs.next();
  }

  @Override
  public void close() throws SQLException {
    rs.close();
  }

  @Override
  public boolean wasNull() throws SQLException {
    return rs.wasNull();
  }

  @Override
  public String getString(int columnIndex) throws SQLException {
    return rs.getString(toPhysical(columnIndex));
  }

  @Override
  public boolean getBoolean(int columnIndex) throws SQLException {
    return rs.getBoolean(toPhysical(columnIndex));
  }

  @Override
  public byte getByte(int columnIndex) throws SQLException {
    return rs.getByte(toPhysical(columnIndex));
  }

  @Override
  public short getShort(int columnIndex) throws SQLException {
    return rs.getShort(toPhysical(columnIndex));
  }

  @Override
  public int getInt(int columnIndex) throws SQLException {
    return rs.getInt(toPhysical(columnIndex));
  }

  @Override
  public long getLong(int columnIndex) throws SQLException {
    return rs.getLong(toPhysical(columnIndex));
  }

  @Override
  public float getFloat(int columnIndex) throws SQLException {
    return rs.getFloat(toPhysical(columnIndex));
  }

  @Override
  public double getDouble(int columnIndex) throws SQLException {
    return rs.getDouble(toPhysical(columnIndex));
  }

  @Override
  public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
    return rs.getBigDecimal(toPhysical(columnIndex), scale);
  }

  @Override
  public byte[] getBytes(int columnIndex) throws SQLException {
    return rs.getBytes(toPhysical(columnIndex));
  }

  @Override
  public Date getDate(int columnIndex) throws SQLException {
    return rs.getDate(toPhysical(columnIndex));
  }

  @Override
  public Time getTime(int columnIndex) throws SQLException {
    return rs.getTime(toPhysical(columnIndex));
  }

  @Override
  public Timestamp getTimestamp(int columnIndex) throws SQLException {
    return rs.getTimestamp(toPhysical(columnIndex));
  }

  @Override
  public InputStream getAsciiStream(int columnIndex) throws SQLException {
    return rs.getAsciiStream(toPhysical(columnIndex));
  }

  @Override
  public InputStream getUnicodeStream(int columnIndex) throws SQLException {
    return rs.getUnicodeStream(toPhysical(columnIndex));
  }

  @Override
  public InputStream getBinaryStream(int columnIndex) throws SQLException {
    return rs.getBinaryStream(toPhysical(columnIndex));
  }

  @Override
  public String getString(String columnLabel) throws SQLException {
    return rs.getString(toPhysical(columnLabel));
  }

  @Override
  public boolean getBoolean(String columnLabel) throws SQLException {
    return rs.getBoolean(toPhysical(columnLabel));
  }

  @Override
  public byte getByte(String columnLabel) throws SQLException {
    return rs.getByte(toPhysical(columnLabel));
  }

  @Override
  public short getShort(String columnLabel) throws SQLException {
    return rs.getShort(toPhysical(columnLabel));
  }

  @Override
  public int getInt(String columnLabel) throws SQLException {
    return rs.getInt(toPhysical(columnLabel));
  }

  @Override
  public long getLong(String columnLabel) throws SQLException {
    return rs.getLong(toPhysical(columnLabel));
  }

  @Override
  public float getFloat(String columnLabel) throws SQLException {
    return rs.getFloat(toPhysical(columnLabel));
  }

  @Override
  public double getDouble(String columnLabel) throws SQLException {
    return rs.getDouble(toPhysical(columnLabel));
  }

  @Override
  public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
    return rs.getBigDecimal(toPhysical(columnLabel), scale);
  }

  @Override
  public byte[] getBytes(String columnLabel) throws SQLException {
    return rs.getBytes(toPhysical(columnLabel));
  }

  @Override
  public Date getDate(String columnLabel) throws SQLException {
    return rs.getDate(toPhysical(columnLabel));
  }

  @Override
  public Time getTime(String columnLabel) throws SQLException {
    return rs.getTime(toPhysical(columnLabel));
  }

  @Override
  public Timestamp getTimestamp(String columnLabel) throws SQLException {
    return rs.getTimestamp(toPhysical(columnLabel));
  }

  @Override
  public InputStream getAsciiStream(String columnLabel) throws SQLException {
    return rs.getAsciiStream(toPhysical(columnLabel));
  }

  @Override
  public InputStream getUnicodeStream(String columnLabel) throws SQLException {
    return rs.getUnicodeStream(toPhysical(columnLabel));
  }

  @Override
  public InputStream getBinaryStream(String columnLabel) throws SQLException {
    return rs.getBinaryStream(toPhysical(columnLabel));
  }

  @Override
  public SQLWarning getWarnings() throws SQLException {
    return rs.getWarnings();
  }

  @Override
  public void clearWarnings() throws SQLException {
    rs.clearWarnings();
  }

  @Override
  public String getCursorName() throws SQLException {
    return rs.getCursorName();
  }

  @Override
  public Object getObject(int columnIndex) throws SQLException {
    return rs.getObject(toPhysical(columnIndex));
  }

  @Override
  public Object getObject(String columnLabel) throws SQLException {
    return rs.getObject(toPhysical(columnLabel));
  }

  @Override
  public int findColumn(String columnLabel) throws SQLException {
    // Return the logical column index for the given logical column label
    Integer logicalIndex = logicalNameToLogicalId.get(columnLabel);
    if (logicalIndex == null) {
      throw new SQLException("Column not found: " + columnLabel);
    }
    return logicalIndex;
  }

  @Override
  public Reader getCharacterStream(int columnIndex) throws SQLException {
    return rs.getCharacterStream(toPhysical(columnIndex));
  }

  @Override
  public Reader getCharacterStream(String columnLabel) throws SQLException {
    return rs.getCharacterStream(toPhysical(columnLabel));
  }

  @Override
  public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
    return rs.getBigDecimal(toPhysical(columnIndex));
  }

  @Override
  public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
    return rs.getBigDecimal(toPhysical(columnLabel));
  }

  @Override
  public boolean isBeforeFirst() throws SQLException {
    return rs.isBeforeFirst();
  }

  @Override
  public boolean isAfterLast() throws SQLException {
    return rs.isAfterLast();
  }

  @Override
  public boolean isFirst() throws SQLException {
    return rs.isFirst();
  }

  @Override
  public boolean isLast() throws SQLException {
    return rs.isLast();
  }

  @Override
  public void beforeFirst() throws SQLException {
    rs.beforeFirst();
  }

  @Override
  public void afterLast() throws SQLException {
    rs.afterLast();
  }

  @Override
  public boolean first() throws SQLException {
    return rs.first();
  }

  @Override
  public boolean last() throws SQLException {
    return rs.last();
  }

  @Override
  public int getRow() throws SQLException {
    return rs.getRow();
  }

  @Override
  public boolean absolute(int row) throws SQLException {
    return rs.absolute(row);
  }

  @Override
  public boolean relative(int rows) throws SQLException {
    return rs.relative(rows);
  }

  @Override
  public boolean previous() throws SQLException {
    return rs.previous();
  }

  @Override
  public void setFetchDirection(int direction) throws SQLException {
    rs.setFetchDirection(direction);
  }

  @Override
  public int getFetchDirection() throws SQLException {
    return rs.getFetchDirection();
  }

  @Override
  public void setFetchSize(int rows) throws SQLException {
    rs.setFetchSize(rows);
  }

  @Override
  public int getFetchSize() throws SQLException {
    return rs.getFetchSize();
  }

  @Override
  public int getType() throws SQLException {
    return rs.getType();
  }

  @Override
  public int getConcurrency() throws SQLException {
    return rs.getConcurrency();
  }

  @Override
  public boolean rowUpdated() throws SQLException {
    return rs.rowUpdated();
  }

  @Override
  public boolean rowInserted() throws SQLException {
    return rs.rowInserted();
  }

  @Override
  public boolean rowDeleted() throws SQLException {
    return rs.rowDeleted();
  }

  @Override
  public void updateNull(int columnIndex) throws SQLException {
    rs.updateNull(toPhysical(columnIndex));
  }

  @Override
  public void updateBoolean(int columnIndex, boolean x) throws SQLException {
    rs.updateBoolean(toPhysical(columnIndex), x);
  }

  @Override
  public void updateByte(int columnIndex, byte x) throws SQLException {
    rs.updateByte(toPhysical(columnIndex), x);
  }

  @Override
  public void updateShort(int columnIndex, short x) throws SQLException {
    rs.updateShort(toPhysical(columnIndex), x);
  }

  @Override
  public void updateInt(int columnIndex, int x) throws SQLException {
    rs.updateInt(toPhysical(columnIndex), x);
  }

  @Override
  public void updateLong(int columnIndex, long x) throws SQLException {
    rs.updateLong(toPhysical(columnIndex), x);
  }

  @Override
  public void updateFloat(int columnIndex, float x) throws SQLException {
    rs.updateFloat(toPhysical(columnIndex), x);
  }

  @Override
  public void updateDouble(int columnIndex, double x) throws SQLException {
    rs.updateDouble(toPhysical(columnIndex), x);
  }

  @Override
  public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
    rs.updateBigDecimal(toPhysical(columnIndex), x);
  }

  @Override
  public void updateString(int columnIndex, String x) throws SQLException {
    rs.updateString(toPhysical(columnIndex), x);
  }

  @Override
  public void updateBytes(int columnIndex, byte[] x) throws SQLException {
    rs.updateBytes(toPhysical(columnIndex), x);
  }

  @Override
  public void updateDate(int columnIndex, Date x) throws SQLException {
    rs.updateDate(toPhysical(columnIndex), x);
  }

  @Override
  public void updateTime(int columnIndex, Time x) throws SQLException {
    rs.updateTime(toPhysical(columnIndex), x);
  }

  @Override
  public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
    rs.updateTimestamp(toPhysical(columnIndex), x);
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
    rs.updateAsciiStream(toPhysical(columnIndex), x, length);
  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
    rs.updateBinaryStream(toPhysical(columnIndex), x, length);
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
    rs.updateCharacterStream(toPhysical(columnIndex), x, length);
  }

  @Override
  public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
    rs.updateObject(toPhysical(columnIndex), x, scaleOrLength);
  }

  @Override
  public void updateObject(int columnIndex, Object x) throws SQLException {
    rs.updateObject(toPhysical(columnIndex), x);
  }

  @Override
  public void updateNull(String columnLabel) throws SQLException {
    rs.updateNull(toPhysical(columnLabel));
  }

  @Override
  public void updateBoolean(String columnLabel, boolean x) throws SQLException {
    rs.updateBoolean(toPhysical(columnLabel), x);
  }

  @Override
  public void updateByte(String columnLabel, byte x) throws SQLException {
    rs.updateByte(toPhysical(columnLabel), x);
  }

  @Override
  public void updateShort(String columnLabel, short x) throws SQLException {
    rs.updateShort(toPhysical(columnLabel), x);
  }

  @Override
  public void updateInt(String columnLabel, int x) throws SQLException {
    rs.updateInt(toPhysical(columnLabel), x);
  }

  @Override
  public void updateLong(String columnLabel, long x) throws SQLException {
    rs.updateLong(toPhysical(columnLabel), x);
  }

  @Override
  public void updateFloat(String columnLabel, float x) throws SQLException {
    rs.updateFloat(toPhysical(columnLabel), x);
  }

  @Override
  public void updateDouble(String columnLabel, double x) throws SQLException {
    rs.updateDouble(toPhysical(columnLabel), x);
  }

  @Override
  public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
    rs.updateBigDecimal(toPhysical(columnLabel), x);
  }

  @Override
  public void updateString(String columnLabel, String x) throws SQLException {
    rs.updateString(toPhysical(columnLabel), x);
  }

  @Override
  public void updateBytes(String columnLabel, byte[] x) throws SQLException {
    rs.updateBytes(toPhysical(columnLabel), x);
  }

  @Override
  public void updateDate(String columnLabel, Date x) throws SQLException {
    rs.updateDate(toPhysical(columnLabel), x);
  }

  @Override
  public void updateTime(String columnLabel, Time x) throws SQLException {
    rs.updateTime(toPhysical(columnLabel), x);
  }

  @Override
  public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
    rs.updateTimestamp(toPhysical(columnLabel), x);
  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
    rs.updateAsciiStream(toPhysical(columnLabel), x, length);
  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x, int length)
      throws SQLException {
    rs.updateBinaryStream(toPhysical(columnLabel), x, length);
  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader, int length)
      throws SQLException {
    rs.updateCharacterStream(toPhysical(columnLabel), reader, length);
  }

  @Override
  public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
    rs.updateObject(toPhysical(columnLabel), x, scaleOrLength);
  }

  @Override
  public void updateObject(String columnLabel, Object x) throws SQLException {
    rs.updateObject(toPhysical(columnLabel), x);
  }

  @Override
  public void insertRow() throws SQLException {
    rs.insertRow();
  }

  @Override
  public void updateRow() throws SQLException {
    rs.updateRow();
  }

  @Override
  public void deleteRow() throws SQLException {
    rs.deleteRow();
  }

  @Override
  public void refreshRow() throws SQLException {
    rs.refreshRow();
  }

  @Override
  public void cancelRowUpdates() throws SQLException {
    rs.cancelRowUpdates();
  }

  @Override
  public void moveToInsertRow() throws SQLException {
    rs.moveToInsertRow();
  }

  @Override
  public void moveToCurrentRow() throws SQLException {
    rs.moveToCurrentRow();
  }

  @Override
  public Statement getStatement() throws SQLException {
    return rs.getStatement();
  }

  @Override
  public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
    return rs.getObject(toPhysical(columnIndex), map);
  }

  @Override
  public Ref getRef(int columnIndex) throws SQLException {
    return rs.getRef(toPhysical(columnIndex));
  }

  @Override
  public Blob getBlob(int columnIndex) throws SQLException {
    return rs.getBlob(toPhysical(columnIndex));
  }

  @Override
  public Clob getClob(int columnIndex) throws SQLException {
    return rs.getClob(toPhysical(columnIndex));
  }

  @Override
  public Array getArray(int columnIndex) throws SQLException {
    return rs.getArray(toPhysical(columnIndex));
  }

  @Override
  public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
    return rs.getObject(toPhysical(columnLabel), map);
  }

  @Override
  public Ref getRef(String columnLabel) throws SQLException {
    return rs.getRef(toPhysical(columnLabel));
  }

  @Override
  public Blob getBlob(String columnLabel) throws SQLException {
    return rs.getBlob(toPhysical(columnLabel));
  }

  @Override
  public Clob getClob(String columnLabel) throws SQLException {
    return rs.getClob(toPhysical(columnLabel));
  }

  @Override
  public Array getArray(String columnLabel) throws SQLException {
    return rs.getArray(toPhysical(columnLabel));
  }

  @Override
  public Date getDate(int columnIndex, Calendar cal) throws SQLException {
    return rs.getDate(toPhysical(columnIndex), cal);
  }

  @Override
  public Date getDate(String columnLabel, Calendar cal) throws SQLException {
    return rs.getDate(toPhysical(columnLabel), cal);
  }

  @Override
  public Time getTime(int columnIndex, Calendar cal) throws SQLException {
    return rs.getTime(toPhysical(columnIndex), cal);
  }

  @Override
  public Time getTime(String columnLabel, Calendar cal) throws SQLException {
    return rs.getTime(toPhysical(columnLabel), cal);
  }

  @Override
  public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
    return rs.getTimestamp(toPhysical(columnIndex), cal);
  }

  @Override
  public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
    return rs.getTimestamp(toPhysical(columnLabel), cal);
  }

  @Override
  public URL getURL(int columnIndex) throws SQLException {
    return rs.getURL(toPhysical(columnIndex));
  }

  @Override
  public URL getURL(String columnLabel) throws SQLException {
    return rs.getURL(toPhysical(columnLabel));
  }

  @Override
  public void updateRef(int columnIndex, Ref x) throws SQLException {
    rs.updateRef(toPhysical(columnIndex), x);
  }

  @Override
  public void updateRef(String columnLabel, Ref x) throws SQLException {
    rs.updateRef(toPhysical(columnLabel), x);
  }

  @Override
  public void updateBlob(int columnIndex, Blob x) throws SQLException {
    rs.updateBlob(toPhysical(columnIndex), x);
  }

  @Override
  public void updateBlob(String columnLabel, Blob x) throws SQLException {
    rs.updateBlob(toPhysical(columnLabel), x);
  }

  @Override
  public void updateClob(int columnIndex, Clob x) throws SQLException {
    rs.updateClob(toPhysical(columnIndex), x);
  }

  @Override
  public void updateClob(String columnLabel, Clob x) throws SQLException {
    rs.updateClob(toPhysical(columnLabel), x);
  }

  @Override
  public void updateArray(int columnIndex, Array x) throws SQLException {
    rs.updateArray(toPhysical(columnIndex), x);
  }

  @Override
  public void updateArray(String columnLabel, Array x) throws SQLException {
    rs.updateArray(toPhysical(columnLabel), x);
  }

  @Override
  public RowId getRowId(int columnIndex) throws SQLException {
    return rs.getRowId(toPhysical(columnIndex));
  }

  @Override
  public RowId getRowId(String columnLabel) throws SQLException {
    return rs.getRowId(toPhysical(columnLabel));
  }

  @Override
  public void updateRowId(int columnIndex, RowId x) throws SQLException {
    rs.updateRowId(toPhysical(columnIndex), x);
  }

  @Override
  public void updateRowId(String columnLabel, RowId x) throws SQLException {
    rs.updateRowId(toPhysical(columnLabel), x);
  }

  @Override
  public int getHoldability() throws SQLException {
    return rs.getHoldability();
  }

  @Override
  public boolean isClosed() throws SQLException {
    return rs.isClosed();
  }

  @Override
  public void updateNString(int columnIndex, String nString) throws SQLException {
    rs.updateNString(toPhysical(columnIndex), nString);
  }

  @Override
  public void updateNString(String columnLabel, String nString) throws SQLException {
    rs.updateNString(toPhysical(columnLabel), nString);
  }

  @Override
  public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
    rs.updateNClob(toPhysical(columnIndex), nClob);
  }

  @Override
  public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
    rs.updateNClob(toPhysical(columnLabel), nClob);
  }

  @Override
  public NClob getNClob(int columnIndex) throws SQLException {
    return rs.getNClob(toPhysical(columnIndex));
  }

  @Override
  public NClob getNClob(String columnLabel) throws SQLException {
    return rs.getNClob(toPhysical(columnLabel));
  }

  @Override
  public SQLXML getSQLXML(int columnIndex) throws SQLException {
    return rs.getSQLXML(toPhysical(columnIndex));
  }

  @Override
  public SQLXML getSQLXML(String columnLabel) throws SQLException {
    return rs.getSQLXML(toPhysical(columnLabel));
  }

  @Override
  public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
    rs.updateSQLXML(toPhysical(columnIndex), xmlObject);
  }

  @Override
  public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
    rs.updateSQLXML(toPhysical(columnLabel), xmlObject);
  }

  @Override
  public String getNString(int columnIndex) throws SQLException {
    return rs.getNString(toPhysical(columnIndex));
  }

  @Override
  public String getNString(String columnLabel) throws SQLException {
    return rs.getNString(toPhysical(columnLabel));
  }

  @Override
  public Reader getNCharacterStream(int columnIndex) throws SQLException {
    return rs.getNCharacterStream(toPhysical(columnIndex));
  }

  @Override
  public Reader getNCharacterStream(String columnLabel) throws SQLException {
    return rs.getNCharacterStream(toPhysical(columnLabel));
  }

  @Override
  public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    rs.updateNCharacterStream(toPhysical(columnIndex), x, length);
  }

  @Override
  public void updateNCharacterStream(String columnLabel, Reader reader, long length)
      throws SQLException {
    rs.updateNCharacterStream(toPhysical(columnLabel), reader, length);
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
    rs.updateAsciiStream(toPhysical(columnIndex), x, length);
  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
    rs.updateBinaryStream(toPhysical(columnIndex), x, length);
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    rs.updateCharacterStream(toPhysical(columnIndex), x, length);
  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x, long length)
      throws SQLException {
    rs.updateAsciiStream(toPhysical(columnLabel), x, length);
  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x, long length)
      throws SQLException {
    rs.updateBinaryStream(toPhysical(columnLabel), x, length);
  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader, long length)
      throws SQLException {
    rs.updateCharacterStream(toPhysical(columnLabel), reader, length);
  }

  @Override
  public void updateBlob(int columnIndex, InputStream inputStream, long length)
      throws SQLException {
    rs.updateBlob(toPhysical(columnIndex), inputStream, length);
  }

  @Override
  public void updateBlob(String columnLabel, InputStream inputStream, long length)
      throws SQLException {
    rs.updateBlob(toPhysical(columnLabel), inputStream, length);
  }

  @Override
  public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
    rs.updateClob(toPhysical(columnIndex), reader, length);
  }

  @Override
  public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
    rs.updateClob(toPhysical(columnLabel), reader, length);
  }

  @Override
  public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
    rs.updateNClob(toPhysical(columnIndex), reader, length);
  }

  @Override
  public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
    rs.updateNClob(toPhysical(columnLabel), reader, length);
  }

  @Override
  public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
    rs.updateNCharacterStream(toPhysical(columnIndex), x);
  }

  @Override
  public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
    rs.updateNCharacterStream(toPhysical(columnLabel), reader);
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
    rs.updateAsciiStream(toPhysical(columnIndex), x);
  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
    rs.updateBinaryStream(toPhysical(columnIndex), x);
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
    rs.updateCharacterStream(toPhysical(columnIndex), x);
  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
    rs.updateAsciiStream(toPhysical(columnLabel), x);
  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
    rs.updateBinaryStream(toPhysical(columnLabel), x);
  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
    rs.updateCharacterStream(toPhysical(columnLabel), reader);
  }

  @Override
  public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
    rs.updateBlob(toPhysical(columnIndex), inputStream);
  }

  @Override
  public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
    rs.updateBlob(toPhysical(columnLabel), inputStream);
  }

  @Override
  public void updateClob(int columnIndex, Reader reader) throws SQLException {
    rs.updateClob(toPhysical(columnIndex), reader);
  }

  @Override
  public void updateClob(String columnLabel, Reader reader) throws SQLException {
    rs.updateClob(toPhysical(columnLabel), reader);
  }

  @Override
  public void updateNClob(int columnIndex, Reader reader) throws SQLException {
    rs.updateNClob(toPhysical(columnIndex), reader);
  }

  @Override
  public void updateNClob(String columnLabel, Reader reader) throws SQLException {
    rs.updateNClob(toPhysical(columnLabel), reader);
  }

  @Override
  public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
    return rs.getObject(toPhysical(columnIndex), type);
  }

  @Override
  public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
    return rs.getObject(toPhysical(columnLabel), type);
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return rs.unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return rs.isWrapperFor(iface);
  }

  // ===== ResultSetMetaData methods
  @Override
  public int getColumnCount() throws SQLException {
    return logicalColumnIdToPhysical.size();
  }

  @Override
  public boolean isAutoIncrement(int column) throws SQLException {
    return rsmd.isAutoIncrement(toPhysical(column));
  }

  @Override
  public boolean isCaseSensitive(int column) throws SQLException {
    return rsmd.isCaseSensitive(toPhysical(column));
  }

  @Override
  public boolean isSearchable(int column) throws SQLException {
    return rsmd.isSearchable(toPhysical(column));
  }

  @Override
  public boolean isCurrency(int column) throws SQLException {
    return rsmd.isCurrency(toPhysical(column));
  }

  @Override
  public int isNullable(int column) throws SQLException {
    return rsmd.isNullable(toPhysical(column));
  }

  @Override
  public boolean isSigned(int column) throws SQLException {
    return rsmd.isSigned(toPhysical(column));
  }

  @Override
  public int getColumnDisplaySize(int column) throws SQLException {
    return rsmd.getColumnDisplaySize(toPhysical(column));
  }

  @Override
  public String getColumnLabel(int column) throws SQLException {
    // Return the logical column label (field name in snake_case)
    return logicalColumnIdToLogicalName.get(column);
  }

  @Override
  public String getColumnName(int column) throws SQLException {
    // Return the logical column name (field name in snake_case)
    return logicalColumnIdToLogicalName.get(column);
  }

  @Override
  public String getSchemaName(int column) throws SQLException {
    return rsmd.getSchemaName(toPhysical(column));
  }

  @Override
  public int getPrecision(int column) throws SQLException {
    return rsmd.getPrecision(toPhysical(column));
  }

  @Override
  public int getScale(int column) throws SQLException {
    return rsmd.getScale(toPhysical(column));
  }

  @Override
  public String getTableName(int column) throws SQLException {
    return rsmd.getTableName(toPhysical(column));
  }

  @Override
  public String getCatalogName(int column) throws SQLException {
    return rsmd.getCatalogName(toPhysical(column));
  }

  @Override
  public int getColumnType(int column) throws SQLException {
    return rsmd.getColumnType(toPhysical(column));
  }

  @Override
  public String getColumnTypeName(int column) throws SQLException {
    return rsmd.getColumnTypeName(toPhysical(column));
  }

  @Override
  public boolean isReadOnly(int column) throws SQLException {
    return rsmd.isReadOnly(toPhysical(column));
  }

  @Override
  public boolean isWritable(int column) throws SQLException {
    return rsmd.isWritable(toPhysical(column));
  }

  @Override
  public boolean isDefinitelyWritable(int column) throws SQLException {
    return rsmd.isDefinitelyWritable(toPhysical(column));
  }

  @Override
  public String getColumnClassName(int column) throws SQLException {
    return rsmd.getColumnClassName(toPhysical(column));
  }
}
