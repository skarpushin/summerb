package org.summerb.easycrud.sql_builder.mysql;

import com.google.common.base.Preconditions;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.sql_builder.FieldsEnlister;
import org.summerb.easycrud.sql_builder.OrderByToSql;
import org.summerb.easycrud.sql_builder.QueryToSql;
import org.summerb.easycrud.sql_builder.model.FromAndWhere;
import org.summerb.easycrud.sql_builder.model.QueryData;
import org.summerb.utils.easycrud.api.dto.PagerParams;

public class SqlBuilderMySqlImpl extends SqlBuilderCommonImpl {

  public SqlBuilderMySqlImpl(
      QuerySpecificsResolver querySpecificsResolver,
      FieldsEnlister fieldsEnlister,
      QueryToSql queryToSql,
      OrderByToSql orderByToSql) {
    super(querySpecificsResolver, fieldsEnlister, queryToSql, orderByToSql);
  }

  @Override
  public QueryData selectMultipleRows(
      Class<?> rowClass,
      FromAndWhere fromAndWhere,
      Query<?, ?> optionalQuery,
      PagerParams pagerParams,
      OrderBy[] orderBy,
      boolean countQueryWillFollow) {
    if (!countQueryWillFollow) {
      return super.selectMultipleRows(
          rowClass, fromAndWhere, optionalQuery, pagerParams, orderBy, countQueryWillFollow);
    }

    Preconditions.checkNotNull(fromAndWhere, "fromAndWhere is required");
    fromAndWhere.getParams().addValue(PagerParams.FIELD_OFFSET, pagerParams.getOffset());
    fromAndWhere.getParams().addValue(PagerParams.FIELD_MAX, pagerParams.getMax());

    String sql =
        "SELECT SQL_CALC_FOUND_ROWS *"
            + fromAndWhere.getSql()
            + orderByToSql.buildOrderBySubclause(orderBy);
    if (!PagerParams.ALL.equals(pagerParams)) {
      sql = sql + sqlPartPaginator;
    }

    return new QueryData(sql, fromAndWhere.getParams());
  }

  @Override
  public QueryData queryForCountAfterPagedSelect(FromAndWhere fromAndWhere) {
    return new QueryData("SELECT FOUND_ROWS()", fromAndWhere.getParams());
  }
}
