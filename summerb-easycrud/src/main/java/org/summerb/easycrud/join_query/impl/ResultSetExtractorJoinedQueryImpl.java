package org.summerb.easycrud.join_query.impl;

import com.google.common.base.Preconditions;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.join_query.model.JoinedRow;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.sql_builder.model.ColumnsSelection;

/**
 * This class is intended to extract results of a joined query (when all columns from more than 1
 * table are retrieved using a single query)
 *
 * <p>NOTE: This is intended to be instantiated every time another query is executed.
 *
 * <p>TBD: Consider if it is possible to make at least some part of it reusable
 */
@SuppressWarnings("resource")
public class ResultSetExtractorJoinedQueryImpl implements ResultSetExtractor<List<JoinedRow>> {
  protected QuerySpecificsResolver querySpecificsResolver;

  protected List<ColumnsSelection> columnSelections;
  protected List<Query<?, ?>> selectedTables;

  protected List<RowMappingAdapter> mappingAdapters;
  protected List<JoinedRow> rows = new ArrayList<>();

  protected int totalResultsCount;

  public ResultSetExtractorJoinedQueryImpl(
      List<Query<?, ?>> selectedTables,
      List<ColumnsSelection> columnSelections,
      QuerySpecificsResolver querySpecificsResolver) {
    Preconditions.checkArgument(selectedTables != null, "selectedTables is required");
    Preconditions.checkArgument(columnSelections != null, "columnSelections is required");
    Preconditions.checkArgument(
        querySpecificsResolver != null, "querySpecificsResolver is required");

    this.selectedTables = selectedTables;
    this.columnSelections = columnSelections;
    this.querySpecificsResolver = querySpecificsResolver;

    initMappingAdapters(selectedTables);
  }

  protected void initMappingAdapters(List<Query<?, ?>> selectedTables) {
    mappingAdapters = new ArrayList<>(selectedTables.size());
    for (Query<?, ?> selectedTable : selectedTables) {
      ColumnsSelection columnsSelection = getColumnsSelection(selectedTable);
      RowMappingAdapter adapter =
          new RowMappingAdapter(
              columnsSelection, this.querySpecificsResolver.getRowMapper(selectedTable));
      mappingAdapters.add(adapter);
    }
  }

  protected ColumnsSelection getColumnsSelection(Query<?, ?> selectedTable) {
    return columnSelections.stream()
        .filter(x -> x.getQuery().equals(selectedTable))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Can't find selected columns for table "
                        + selectedTable.getService().getRowMessageCode()));
  }

  @Override
  public List<JoinedRow> extractData(ResultSet rs) throws SQLException, DataAccessException {
    ResultSetMetaData rsmd = rs.getMetaData();

    List<String> columnNames = buildColumnsList(rsmd);

    for (RowMappingAdapter x : mappingAdapters) {
      x.setResultSet(rs, rsmd, columnNames);
    }

    int rowNum = 0;
    while (rs.next()) {
      rows.add(mapRow(rowNum++));
    }
    return rows;
  }

  protected List<String> buildColumnsList(ResultSetMetaData rsmd) throws SQLException {
    List<String> columnNames = new ArrayList<>(rsmd.getColumnCount());
    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
      columnNames.add(rsmd.getColumnLabel(i));
    }
    return columnNames;
  }

  protected JoinedRow mapRow(int rowNum) throws SQLException, DataAccessException {
    JoinedRowImpl row = new JoinedRowImpl();
    for (RowMappingAdapter part : mappingAdapters) {
      Object subRow = part.mapRow(rowNum);
      if (subRow != null) {
        row.put(part.query, subRow);
      }
    }
    return row;
  }

  /**
   * @return map of mapped rows. Key = ID, Value Mapped (deserialized) value itself
   */
  public Map<?, ? extends HasId<?>> getMappedRows(Query<?, ?> query) {
    Preconditions.checkArgument(query != null, "query is required");
    return mappingAdapters.stream()
        .filter(x -> x.query.equals(query))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("query not found"))
        .mapIdToRow;
  }

  public int getTotalResultsCount() {
    return totalResultsCount;
  }

  public void setTotalResultsCount(int totalResultsCount) {
    this.totalResultsCount = totalResultsCount;
  }
}
