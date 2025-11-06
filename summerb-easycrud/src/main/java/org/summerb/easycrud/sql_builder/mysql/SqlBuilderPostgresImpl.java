package org.summerb.easycrud.sql_builder.mysql;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.OrderByQueryResolver;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.sql_builder.FieldsEnlister;
import org.summerb.easycrud.sql_builder.OrderByToSql;
import org.summerb.easycrud.sql_builder.QueryToSql;
import org.summerb.easycrud.sql_builder.model.ColumnsSelection;
import org.summerb.easycrud.sql_builder.model.SelectedColumn;

public class SqlBuilderPostgresImpl extends SqlBuilderCommonImpl {

  public SqlBuilderPostgresImpl(
      QuerySpecificsResolver querySpecificsResolver,
      FieldsEnlister fieldsEnlister,
      QueryToSql queryToSql,
      OrderByToSql orderByToSql) {
    super(querySpecificsResolver, fieldsEnlister, queryToSql, orderByToSql);
  }

  /**
   * We override this method to check if we have collation on at least one participating column. If
   * se we prevent wildcard from being used, so that all selected columns are listed explicitly and
   * thus give a change to {@link #appendColumnSelection(JoinQuery, Query, OrderBy[], String,
   * boolean, boolean, StringBuilder)} to add collation and proper alias to such a column
   */
  @Override
  protected <TId, TRow extends HasId<TId>> void appendColumnsSelection(
      Class<?> rowClass,
      JoinQuery<?, ?> optionalJoinQuery,
      Query<TId, TRow> optionalQuery,
      OrderBy[] orderBy,
      boolean wildcardAllowed,
      boolean prefixColumnsWhenReferencing,
      boolean selectAsPrefixedAliasedNames,
      StringBuilder outSql,
      List<ColumnsSelection> outColumns) {

    boolean hasOrderBy = orderBy != null && orderBy.length > 0;
    boolean assumingMultipleQueries = optionalJoinQuery != null;
    boolean orderByExpectedToHaveLinkToQuery = optionalJoinQuery != null;

    if (!wildcardAllowed) {
      super.appendColumnsSelection(
          rowClass,
          optionalJoinQuery,
          optionalQuery,
          orderBy,
          false,
          prefixColumnsWhenReferencing,
          selectAsPrefixedAliasedNames,
          outSql,
          outColumns);
    } else {
      boolean isCurrentQueryHasFieldsOrderedWithCollation =
          hasOrderBy
              && Arrays.stream(orderBy)
                  .anyMatch(
                      x ->
                          x.getCollate() != null
                              && (!orderByExpectedToHaveLinkToQuery
                                  || OrderByQueryResolver.get(x) == optionalQuery));
      boolean newWildcardAllowed = !isCurrentQueryHasFieldsOrderedWithCollation;
      super.appendColumnsSelection(
          rowClass,
          optionalJoinQuery,
          optionalQuery,
          orderBy,
          newWildcardAllowed,
          prefixColumnsWhenReferencing,
          selectAsPrefixedAliasedNames,
          outSql,
          outColumns);
    }

    if (!hasOrderBy) {
      return;
    }

    if (!assumingMultipleQueries) {
      return;
    }
  }

  /**
   * Postgres requires fields mentioned in ORDER BY to also present in SELECT. So if we have fields
   * from secondary tables in order by, that we do not already have in fields selection, we need to
   * include them too
   */
  @Override
  protected <TId, TRow extends HasId<TId>> void appendAdditionalColumnsSelectionIfNeeded(
      Class<?> rowClass,
      JoinQuery<?, ?> optionalJoinQuery,
      Query<TId, TRow> optionalQuery,
      OrderBy[] orderBy,
      boolean wildcardAllowed,
      boolean prefixColumnsWhenReferencing,
      boolean selectAsPrefixedAliasedNames,
      StringBuilder outSql,
      List<ColumnsSelection> outColumns) {
    super.appendAdditionalColumnsSelectionIfNeeded(
        rowClass,
        optionalJoinQuery,
        optionalQuery,
        orderBy,
        wildcardAllowed,
        prefixColumnsWhenReferencing,
        selectAsPrefixedAliasedNames,
        outSql,
        outColumns);

    if (optionalJoinQuery == null || orderBy == null || orderBy.length == 0) {
      return;
    }

    List<OrderBy> orderByFromOtherTables =
        Arrays.stream(orderBy).filter(x -> !isFieldAlreadySelected(x, outColumns)).toList();
    if (orderByFromOtherTables.isEmpty()) {
      return;
    }

    // Append such columns to sql
    Multimap<Query<?, ?>, SelectedColumn> additionalColumns = ArrayListMultimap.create();
    for (OrderBy orderByFromOtherTable : orderByFromOtherTables) {
      Query<?, ?> otherQuery = OrderByQueryResolver.get(orderByFromOtherTable);

      outSql.append(", ");
      SelectedColumn selectedColumn =
          appendColumnSelection(
              optionalJoinQuery,
              otherQuery,
              orderBy,
              orderByFromOtherTable.getFieldName(),
              prefixColumnsWhenReferencing,
              selectAsPrefixedAliasedNames,
              outSql);
      additionalColumns.put(otherQuery, selectedColumn);
    }

    // Now also add them to result
    for (Query<?, ?> otherQuery : additionalColumns.keySet()) {
      ColumnsSelection retColumns = new ColumnsSelection();
      retColumns.setColumns(new ArrayList<>(additionalColumns.get(otherQuery)));
      retColumns.setQuery(otherQuery);
      outColumns.add(retColumns);
    }
  }

  protected boolean isFieldAlreadySelected(OrderBy orderBy, List<ColumnsSelection> outColumns) {
    Query<?, ?> query = OrderByQueryResolver.get(orderBy);
    return outColumns.stream()
        .filter(x -> x.getQuery().equals(query))
        .flatMap(x -> x.getColumns() != null ? x.getColumns().stream() : Stream.empty())
        .anyMatch(x -> x.getFieldName().equals(orderBy.getFieldName()));
  }

  protected SelectedColumn appendColumnSelection(
      JoinQuery<?, ?> optionalJoinQuery,
      Query<?, ?> optionalQuery,
      OrderBy[] orderBy,
      String fieldName,
      boolean prefixColumnsWhenReferencing,
      boolean selectAsPrefixedAliasedNames,
      StringBuilder sql) {

    if (prefixColumnsWhenReferencing) {
      sql.append(optionalQuery.getAlias()).append(".");
    }

    String columnName = QueryToSqlMySqlImpl.snakeCase(fieldName);
    sql.append(columnName);

    String columnLabel;
    if (selectAsPrefixedAliasedNames) {
      Preconditions.checkState(
          optionalQuery != null && optionalQuery.getAlias() != null,
          "prefixWithAlias == true, while query is either missing or missing alias");
      columnLabel = optionalQuery.getAlias() + "_" + columnName;
    } else {
      columnLabel = columnName;
    }

    // NOTE: Postgres requires columns selection to also contain collation if same is present in
    // order by
    OrderBy collatedOrderBy =
        findCollatedOrderBy(optionalJoinQuery != null, optionalQuery, orderBy, fieldName);
    if (collatedOrderBy != null) {
      sql.append(" COLLATE \"")
          .append(collatedOrderBy.getCollate())
          .append("\"")
          .append(" AS ")
          .append(columnLabel);
    } else if (selectAsPrefixedAliasedNames) {
      sql.append(" AS ").append(columnLabel);
    }
    return new SelectedColumn(fieldName, columnName, columnLabel);
  }

  protected OrderBy findCollatedOrderBy(
      boolean requireQueryMatch, Query<?, ?> query, OrderBy[] orderBy, String fieldName) {
    if (orderBy == null || orderBy.length == 0) {
      return null;
    }
    return Arrays.stream(orderBy)
        .filter(
            x ->
                x.getCollate() != null
                    && (!requireQueryMatch || OrderByQueryResolver.get(x) == query)
                    && x.getFieldName().equals(fieldName))
        .findFirst()
        .orElse(null);
  }
}
