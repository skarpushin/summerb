package org.summerb.easycrud.sql_builder.mysql;

import com.google.common.base.Preconditions;
import java.util.List;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.OrderByQueryResolver;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.sql_builder.OrderByToSql;
import org.summerb.easycrud.sql_builder.model.ColumnsSelection;
import org.summerb.easycrud.sql_builder.model.SelectedColumn;

public class OrderByToSqlMySqlImpl implements OrderByToSql {

  public OrderByToSqlMySqlImpl() {}

  @Override
  public String buildOrderBySubclause(OrderBy[] orderByArr) {
    if (orderByArr == null || orderByArr.length == 0) {
      return "";
    }

    StringBuilder ret = new StringBuilder();
    for (OrderBy orderBy : orderByArr) {
      if (orderBy == null || !StringUtils.hasText(orderBy.getFieldName())) {
        continue;
      }

      if (!ret.isEmpty()) {
        ret.append(", ");
      }

      appendFieldName(ret, orderBy);
      appendCollation(ret, orderBy);
      appendDirection(orderBy, ret);
      appendNullsHandling(orderBy, ret);
    }
    return ret.isEmpty() ? "" : "\nORDER BY " + ret;
  }

  @Override
  public void appendOrderByElements(
      OrderBy[] orderByArr,
      JoinQuery<?, ?> joinQuery,
      List<ColumnsSelection> columnSelections,
      StringBuilder ret) {
    if (orderByArr == null) {
      return;
    }

    boolean added = false;
    for (OrderBy orderBy : orderByArr) {
      Preconditions.checkArgument(
          orderBy != null && StringUtils.hasText(orderBy.getFieldName()), "valid orderBy required");

      Query<?, ?> query = OrderByQueryResolver.get(orderBy);
      Preconditions.checkNotNull(
          query,
          "Can't find query for order by field '%s'. OrderBy for join query must be instantiated via Query class.",
          orderBy.getFieldName());

      if (added) {
        ret.append(", ");
      }

      if (orderBy.getFieldName().contains(".")) {
        // This might be the case when fields were already pre-processed and prefixed with
        // necessary aliases, i.e., as a part of the parsing / translation effort
        // TODO: Add better explanation as to when this might happen
        // TODO: Do we really need this? I though all orderBy must be converted to ones, which has
        //  query reference?
        appendFieldName(ret, orderBy);
      } else {
        appendFieldName(ret, orderBy, query, joinQuery, columnSelections);
      }
      appendCollation(ret, orderBy);
      appendDirection(orderBy, ret);
      appendNullsHandling(orderBy, ret);

      added = true;
    }
  }

  /**
   * @param sql sql to append to
   * @param orderBy orderBy item that needs to be formatted into sql
   * @param query query that was resolved from orderBy
   * @param joinQuery the JoinQuery in context of which this operation is performed
   * @param columnSelections all columns selections for this query
   */
  protected void appendFieldName(
      StringBuilder sql,
      OrderBy orderBy,
      Query<?, ?> query,
      JoinQuery<?, ?> joinQuery,
      List<ColumnsSelection> columnSelections) {

    ColumnsSelection columnsSelection =
        columnSelections.stream().filter(x -> x.getQuery() == query).findFirst().orElse(null);
    if (columnsSelection != null && !columnsSelection.isWildcardAdded()) {
      SelectedColumn selectedColumn =
          columnsSelection.getColumns().stream()
              .filter(x -> x.getFieldName().equals(orderBy.getFieldName()))
              .findFirst()
              .orElse(null);
      if (selectedColumn != null) {
        sql.append(selectedColumn.getColumnLabel());
        return;
      }
    }

    sql.append(query.getAlias())
        .append(".")
        .append(QueryToSqlMySqlImpl.snakeCase(orderBy.getFieldName()));
  }

  protected void appendFieldName(StringBuilder ret, OrderBy orderBy) {
    ret.append(QueryToSqlMySqlImpl.snakeCase(orderBy.getFieldName()));
  }

  protected void appendNullsHandling(OrderBy orderBy, StringBuilder ret) {
    if (orderBy.getNullsLast() != null) {
      throw new IllegalArgumentException("MySQL doesn't support NULLS LAST/FIRST clause");
    }
  }

  protected void appendDirection(OrderBy orderBy, StringBuilder ret) {
    if (OrderBy.ORDER_ASC.equals(orderBy.getDirection())) {
      return;
    }
    if (orderBy.getDirection() != null
        && !OrderBy.ORDER_ASC.equalsIgnoreCase(orderBy.getDirection())) {
      ret.append(" ").append(orderBy.getDirection());
    }
  }

  protected void appendCollation(StringBuilder ret, OrderBy orderBy) {
    if (StringUtils.hasText(orderBy.getCollate())) {
      ret.append(" COLLATE ").append(orderBy.getCollate());
    }
  }
}
